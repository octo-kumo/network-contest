package z.yun.contest.server;

import z.yun.contest.data.Participant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class ParticipantList extends JPanel {
    private final ContestHost host;
    private final Box box;

    public ParticipantList(final ContestHost host) {
        this.host = host;
        box = Box.createVerticalBox();
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setPreferredSize(new Dimension(150, 400));
        setLayout(new BorderLayout());
        add(new JLabel("Participants"), BorderLayout.NORTH);
        add(new JScrollPane(box), BorderLayout.CENTER);
        host.participants.listen(this::update);
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
