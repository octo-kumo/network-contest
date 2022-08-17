package z.yun.contest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import z.yun.contest.server.ContestHost;

public class Participant {
    public String id;
    public String name = "hey";
    public int score;

    public Participant() {
    }

    public Participant(String id, String name) {
        this(id, name, 0);
    }

    public Participant(String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    @JsonCreator
    public Participant(String json) {
        try {
            Participant participant = ContestHost.mapper.readValue(json, Participant.class);
            this.id = participant.id;
            this.name = participant.name;
            this.score = participant.score;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
