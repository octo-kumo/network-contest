package z.yun.contest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.Nullable;
import z.yun.contest.server.ContestHost;

public class Participant {
    public @Nullable String id;
    public String name = "hey";
    public Answer[] answers;
    public int score;
    public int place;

    public Participant() {
    }

    public Participant(String id, String name) {
        this(id, name, 0);
    }

    public Participant(@Nullable String id, String name, int score) {
        this.id = id;
        this.name = name;
        this.score = score;
        answers = new Answer[0];
    }

    @JsonCreator
    public Participant(String json) {
        try {
            Participant participant = ContestHost.mapper.readValue(json, Participant.class);
            this.id = participant.id;
            this.name = participant.name;
            this.score = participant.score;
            this.place = participant.place;
            this.answers = participant.answers;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
