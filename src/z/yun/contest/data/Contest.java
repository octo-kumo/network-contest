package z.yun.contest.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jetbrains.annotations.Nullable;
import z.yun.contest.server.ContestHost;

import java.util.ArrayList;
import java.util.Comparator;

public class Contest {
    public String title;

    public @Nullable String image;
    public String description;
    public String hostedBy;

    public int current_index = -1;
    public boolean acceptingAnswers = false;

    public ArrayList<Question> questions;
    @JsonIgnore
    public final ArrayList<Participant> participants;

    public Contest() {
        questions = new ArrayList<>();
        participants = new ArrayList<>();
    }

    public Contest(String title, String description, String hostedBy) {
        this();
        this.title = title;
        this.description = description;
        this.hostedBy = hostedBy;
    }

    public void revalidate() {
        for (int i = 0; i < questions.size(); i++) questions.get(i).index = i;
    }

    public void updateScore() {
        for (Participant participant : participants) ContestHost.updateScore(participant, questions, current_index);
        participants.sort(Comparator.comparing(p -> p.score, Comparator.reverseOrder()));
        int r = 0, l = Integer.MAX_VALUE;
        for (Participant p : participants) {
            if (p.score < l) {
                r++;
                l = p.score;
            }
            p.place = r;
        }
    }
}
