package z.yun.contest.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import z.yun.contest.data.Contest;
import z.yun.contest.data.Participant;
import z.yun.contest.observable.Observable;
import z.yun.contest.server.ContestHost;

import java.net.URI;

import static io.socket.client.Socket.*;

public class ContestClient {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String EVENT_HANDSHAKE = "handshake";
    public static final String EVENT_ANSWER = "answer";

    public enum Status {
        CONNECTED, CONNECTING, DISCONNECTED
    }

    private final Socket socket;
    public final Observable<Participant> participant = new Observable<>();

    public final Observable<Long> ping = new Observable<>(0L);
    public final Observable<Contest> contest = new Observable<>();
    public final Observable<Status> status = new Observable<>(Status.DISCONNECTED);

    public ContestClient(String host, Participant participant) {
        this.participant.set(participant);
        socket = IO.socket(URI.create(host));
        on(EVENT_CONNECT, e -> {
            this.participant.get().id = socket.id();
            status.set(Status.CONNECTED);
            emit(EVENT_HANDSHAKE, participant);
        });
        on(EVENT_CONNECTING, os -> status.set(Status.CONNECTING));
        on(EVENT_DISCONNECT, os -> {
            this.participant.get().id = null;
            status.set(Status.DISCONNECTED);
        });
        status.set(socket.connected() ? Status.CONNECTED : Status.DISCONNECTED);
        on(ContestHost.EVENT_SERVER_STATUS, e -> {
            try {
                contest.set(mapper.readValue(e[0].toString(), Contest.class));
                if (e.length > 1) this.participant.set(mapper.readValue(e[1].toString(), Participant.class));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });
        on(ContestHost.EVENT_SERVER_PING, e -> ping.set(System.currentTimeMillis() - (long) e[0]));
        on(ContestHost.EVENT_SERVER_SET_PARTICIPANT, e -> {
            try {
                this.participant.set(mapper.readValue(e[0].toString(), Participant.class));
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void connect() {
        socket.connect();
    }

    public void disconnect() {
        socket.disconnect();
    }

    public Emitter on(String event, Emitter.Listener fn) {
        return socket.on(event, fn);
    }

    public Emitter io_on(String event, Emitter.Listener fn) {
        return socket.io().on(event, fn);
    }

    public Emitter emit(final String event, final Object... args) {
        return socket.emit(event, args);
    }

    public Emitter emit(final String event, final Object arg) {
        try {
            return socket.emit(event, mapper.writeValueAsString(arg));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
