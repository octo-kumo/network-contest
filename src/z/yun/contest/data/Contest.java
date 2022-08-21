package z.yun.contest.data;

import java.util.ArrayList;

public class Contest {
    public String title;
    public String description;
    public String hostedBy;
    public String status;

    public int current_index = -1;

    public ArrayList<Question> questions;
    public ArrayList<Participant> participants;

    public Contest() {
        questions = new ArrayList<>();
        participants = new ArrayList<>();
    }

    public Contest(String title, String description, String hostedBy, String status) {
        this();
        this.title = title;
        this.description = description;
        this.hostedBy = hostedBy;
        this.status = status;
    }

    public void revalidate() {
        for (int i = 0; i < questions.size(); i++) questions.get(i).index = i;
    }
}