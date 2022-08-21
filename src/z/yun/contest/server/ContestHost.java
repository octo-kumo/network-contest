package z.yun.contest.server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import z.yun.contest.client.ContestClient;
import z.yun.contest.data.Contest;
import z.yun.contest.data.Participant;
import z.yun.contest.data.Question;
import z.yun.contest.observable.Observable;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ContestHost extends SocketIOServer {
    public static Logger LOGGER = LoggerFactory.getLogger(ContestHost.class);

    public static final String EVENT_SERVER_PING = "server_ping";
    public static final String EVENT_SERVER_STATUS = "server_status";
    public static final String EVENT_SERVER_KICK = "server_kick";
    public static final ObjectMapper mapper = new ObjectMapper();
    public final int port;

    static Configuration getConfig(int port) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(port);
        return config;
    }

    public final Contest contest;
    public final Observable<File> savedTo = new Observable<>();
    public final Observable<ArrayList<Question>> questions = new Observable<>();
    public final Observable<ArrayList<Participant>> participants = new Observable<>();
    private final Timer timer;

    public ContestHost(Contest contest, int port) {
        super(getConfig(port));
        this.contest = contest;
        this.port = port;
        questions.set(this.contest.questions);
        participants.set(this.contest.participants);
        addEventListener(ContestClient.EVENT_HANDSHAKE, Participant.class, (client, data, ackSender) -> {
            if (this.contest.participants.stream().anyMatch(p -> Objects.equals(p.name, data.name))) {
                client.sendEvent(EVENT_SERVER_KICK, "Name taken!");
                LOGGER.info("Rejected for name dupe : " + client.getSessionId() + " : " + data.name);
                return;
            }

            client.sendEvent(EVENT_SERVER_STATUS, getStatus());
            this.contest.participants.add(data);
            participants.set(this.contest.participants);
            LOGGER.info("Player Joined : " + client.getSessionId() + " : " + data.name);
        });
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
        this.timer = new Timer(1000, e -> {
            getBroadcastOperations().sendEvent(EVENT_SERVER_PING, System.currentTimeMillis());
        });
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
    }

    public void refresh() {
        LOGGER.info("sending update to all clients\n" + getStatus());
        getBroadcastOperations().sendEvent(EVENT_SERVER_STATUS, getStatus());
    }
}
