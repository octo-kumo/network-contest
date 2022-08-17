package z.yun.contest.server;

import z.yun.contest.data.Participant;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ParticipantList extends JPanel {
    private final ContestHost contest;
    private final Box box;

    public ParticipantList(final ContestHost contest) {
        this.contest = contest;
        box = Box.createVerticalBox();

        setLayout(new BorderLayout());
        add(new JLabel("Participants"), BorderLayout.NORTH);
        add(new JScrollPane(box), BorderLayout.CENTER);
        contest.participants.listen(this::update);
    }

    public void update(ArrayList<Participant> participants) {
        System.out.println("update" + participants);
        if (participants == null) return;
        box.removeAll();
        participants.forEach(p -> box.add(new JLabel(p.name)));
        revalidate();
        repaint();
    }
}
