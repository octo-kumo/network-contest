package z.yun.contest.server;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import z.yun.contest.client.ContestClient;
import z.yun.contest.data.Contest;
import z.yun.contest.data.Participant;
import z.yun.contest.data.Question;
import z.yun.contest.observable.Observable;

import javax.swing.*;
import java.util.ArrayList;

public class ContestHost extends SocketIOServer {
    public static final String EVENT_SERVER_PING = "server_ping";
    public static final String EVENT_SERVER_STATUS = "server_status";
    public static final ObjectMapper mapper = new ObjectMapper();

    static Configuration getConfig(int port) {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(port);
        return config;
    }

    public final Contest contest;
    public final Observable<ArrayList<Question>> questions = new Observable<>();
    public final Observable<ArrayList<Participant>> participants = new Observable<>();
    private final Timer timer;

    public ContestHost(Contest contest, int port) {
        super(getConfig(port));
        this.contest = contest;
        questions.set(this.contest.questions);
        participants.set(this.contest.participants);
        addEventListener(ContestClient.EVENT_HANDSHAKE, Participant.class, (client, data, ackSender) -> {
            client.sendEvent(EVENT_SERVER_STATUS, this.contest);
            this.contest.participants.add(data);
            System.out.println(data.name);
            participants.set(this.contest.participants);
        });
        this.timer = new Timer(100, e -> getBroadcastOperations().sendEvent(EVENT_SERVER_PING, System.currentTimeMillis()));
    }

    public void start() {
        super.start();
        this.timer.start();
    }

    public void stop() {
        super.stop();
        timer.stop();
    }
}
