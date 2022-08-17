package z.yun.contest.server;

import z.yun.contest.data.Participant;
import z.yun.contest.data.Question;

import java.util.ArrayList;

public class Contest {
    public String title;
    public String description;
    public String hostedBy;
    public String status;

    public int current_index = -1;

    public Question[] questions;
    public ArrayList<Participant> participants;

    public Contest() {
        participants = new ArrayList<>();
    }

    public Contest(String title, String description, String hostedBy, String status) {
        this();
        this.title = title;
        this.description = description;
        this.hostedBy = hostedBy;
        this.status = status;
    }
}
