package z.yun.contest.server;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.alexdlaird.ngrok.NgrokClient;
import com.github.alexdlaird.ngrok.conf.JavaNgrokConfig;
import com.github.alexdlaird.ngrok.protocol.CreateTunnel;
import com.github.alexdlaird.ngrok.protocol.Proto;
import com.github.alexdlaird.ngrok.protocol.Tunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import z.yun.contest.client.ContestClient;
import z.yun.contest.data.Answer;
import z.yun.contest.data.Contest;
import z.yun.contest.data.Participant;
import z.yun.contest.data.Question;
import z.yun.contest.observable.Bindable;
import z.yun.contest.observable.Bindings;
import z.yun.contest.observable.Observable;

import javax.swing.Timer;
import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.stream.IntStream;

public class ContestHost extends SocketIOServer {
    public static Logger LOGGER = LoggerFactory.getLogger(ContestHost.class);

    public static final String EVENT_SERVER_PING = "server_ping";
    public static final String EVENT_SERVER_STATUS = "server_status";
    public static final String EVENT_SERVER_KICK = "server_kick";
    public static final String EVENT_SERVER_SET_PARTICIPANT = "server_set_participant";
    public static final ObjectMapper mapper = new ObjectMapper();
    public final int port;
    private NgrokClient ngrokClient;

    static Configuration getConfig(int port) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(port);
        return config;
    }

    public final Contest contest;
    public final Observable<String> publicURL = new Observable<>();
    public final Observable<File> savedTo = new Observable<>();
    public final Observable<ArrayList<Question>> questions = new Observable<>();
    public final Observable<ArrayList<Participant>> participants = new Observable<>();

    public final Observable<HashMap<String, Answer>[]> answers = new Observable<>();
    public final Bindable<Integer> answerCount;
    private final Timer timer;

    public ContestHost(Contest contest, int port) {
        super(getConfig(port));
        this.contest = contest;
        this.port = port;
        questions.listen(e -> clearAnswers());
        questions.set(this.contest.questions);
        participants.set(this.contest.participants);
        answerCount = Bindings.map(answers, a -> this.contest.current_index < 0 ? 0 : a[this.contest.current_index].size());
        addEventListener(ContestClient.EVENT_HANDSHAKE, Participant.class, this::onHandShake);
        addEventListener(ContestClient.EVENT_ANSWER, Answer.class, this::onAnswer);
        addConnectListener(client -> {
            try {
                LOGGER.info("Client Connection " + client.getSessionId() + " : " + client.getRemoteAddress() + " : " + mapper.writeValueAsString(client.getHandshakeData()));
            } catch (JsonProcessingException ignored) {
            }
        });
        addDisconnectListener(client -> {
            LOGGER.info("Client Disconnection " + client.getSessionId());
            this.contest.participants.removeIf(p -> Objects.equals(p.id, client.getSessionId().toString()));
            participants.set(this.contest.participants);
        });
        this.timer = new Timer(1000, e -> getBroadcastOperations().sendEvent(EVENT_SERVER_PING, System.currentTimeMillis()));
    }

    private JsonNode getStatus() {
        ObjectNode jsonNode = mapper.valueToTree(this.contest);
        ArrayNode questions = (ArrayNode) jsonNode.get("questions");
        for (int i = Math.max(0, this.contest.current_index + 1); i < questions.size(); i++) questions.set(i, null);
        jsonNode.set("questions", questions);
        return jsonNode;
    }

    public void start() {
        super.start();
        this.timer.start();
    }

    public void stop() {
        super.stop();
        timer.stop();
        if (ngrokClient != null) {
            if (!publicURL.isNull()) ngrokClient.disconnect(publicURL.get());
            ngrokClient.getNgrokProcess().stopMonitorThread();
        }
    }

    public void refresh() {
        JsonNode status = getStatus();
        LOGGER.info("sending update to all clients\n" + status);
        getBroadcastOperations().sendEvent(EVENT_SERVER_STATUS, status);
    }

    private void onHandShake(SocketIOClient client, Participant data, AckRequest ack) {
        synchronized (this.contest.participants) {
            if (data.name.isEmpty()) {
                client.sendEvent(EVENT_SERVER_KICK, "Name can't be empty!");
                return;
            }
            if (this.contest.participants.stream().anyMatch(p -> Objects.equals(p.name, data.name))) {
                client.sendEvent(EVENT_SERVER_KICK, "Name taken!");
                LOGGER.info("Rejected for name dupe : " + client.getSessionId() + " : " + data.name);
                return;
            }
            data.id = client.getSessionId().toString();
            data.score = 0;
            data.answers = new Answer[contest.questions.size()];
            contest.participants.add(data);
            contest.updateScore();
            participants.set(this.contest.participants);
            client.sendEvent(EVENT_SERVER_STATUS, getStatus(), data);
            LOGGER.info("Player Joined : " + client.getSessionId() + " : " + data.name);
        }
    }


    private void onAnswer(SocketIOClient client, Answer data, AckRequest ack) {
        contest.participants.stream().filter(p -> Objects.equals(p.id, client.getSessionId().toString())).findAny().ifPresent(p -> {
            if (p.answers[contest.current_index] == null) {
                HashMap<String, Answer> answers = Objects.requireNonNull(this.answers.get())[contest.current_index];
                answers.put(client.getSessionId().toString(), data);
                p.answers[contest.current_index] = data;
                updateScore(p, contest.questions, contest.current_index);
                client.sendEvent(EVENT_SERVER_SET_PARTICIPANT, p);

                LOGGER.info(client.getSessionId() + " : " + data.type + " = " + data.answer + "/" + Arrays.toString(data.choice));
                this.answers.ping();
            }
        });
        contest.participants.sort(Comparator.comparing(p -> p.score, Comparator.reverseOrder()));
        participants.ping();
    }

    public static void updateScore(Participant participant, ArrayList<Question> questions, int current_index) {
        int score = 0;
        if (current_index == -2) current_index = questions.size();
        else current_index++;
        for (int i = 0; i < current_index; i++) {
            Question question = questions.get(i);
            if (participant.answers[i] == null) continue;
            if (question.type == Question.Type.TEXT) {
                if (Objects.equals(question.answer, participant.answers[i].answer)) score++;
            } else if (Arrays.equals(question.correct, participant.answers[i].choice)) score++;
        }
        participant.score = score * 100;
    }

    public void clearAnswers() {
        answers.set(IntStream.range(0, questions.get().size()).mapToObj(i -> new HashMap<String, Answer>()).toArray(HashMap[]::new));
        participants.getAsOptional().ifPresent(a -> a.forEach(p -> {
            if (p.id != null) {
                Arrays.fill(p.answers, null);
                p.score = 0;
                p.place = 1;
                getClient(UUID.fromString(p.id)).sendEvent(ContestHost.EVENT_SERVER_SET_PARTICIPANT, p);
            }
        }));
        participants.ping();
    }

    public void ngrok(Runnable callback, Runnable error) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (ngrokClient == null)
                    ngrokClient = new NgrokClient.Builder().withJavaNgrokConfig(new JavaNgrokConfig.Builder().withAuthToken("2Di89mO3b2HULotECM2Edc9K00f_7kTGYRKq3C9bWQj6X1FoB").build()).build();
                if (!publicURL.isNull()) {
                    if (JOptionPane.showConfirmDialog(null, "Change Address?", "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
                        return;
                    ngrokClient.disconnect(publicURL.get());
                }
                final CreateTunnel sshCreateTunnel = new CreateTunnel.Builder()
                        .withProto(Proto.TCP)
                        .withAddr(port)
                        .build();
                Tunnel sshTunnel = ngrokClient.connect(sshCreateTunnel);
                publicURL.set(sshTunnel.getPublicUrl());
                LOGGER.info(String.format("NGROK Server %s started on %s", sshTunnel.getName(), sshTunnel.getPublicUrl()));
                callback.run();
            } catch (Exception e) {
                LOGGER.error("NGROK Launch Failed", e);
                error.run();
            }
        });
    }
}
