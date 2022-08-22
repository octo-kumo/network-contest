package z.yun.contest.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import z.yun.contest.server.ContestHost;

public class Answer {
    public Question.Type type;
    public boolean[] choice;
    public String answer;

    public Answer(Question.Type type, Object payload) {
        this.type = type;
        if (type == Question.Type.TEXT) this.answer = String.valueOf(payload);
        else this.choice = (boolean[]) payload;
    }

    public Answer() {
    }

    @JsonCreator
    public Answer(String json) {
        try {
            Answer answer = ContestHost.mapper.readValue(json, Answer.class);
            this.type = answer.type;
            this.choice = answer.choice;
            this.answer = answer.answer;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
