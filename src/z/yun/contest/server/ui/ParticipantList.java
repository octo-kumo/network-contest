package z.yun.contest.server.ui;

import z.yun.contest.Fonts;
import z.yun.contest.data.Participant;
import z.yun.contest.server.ContestHost;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.UUID;

public class ParticipantList extends JPanel {
    private final ContestHost host;
    private final JPanel box;

    public ParticipantList(final ContestHost host) {
        this.host = host;
        box = new JPanel(new GridBagLayout());
        box.setBorder(new EmptyBorder(5, 5, 5, 5));
//        box.setBorder(BorderFactory.createDashedBorder(Color.RED));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setPreferredSize(new Dimension(150, 400));
        setLayout(new BorderLayout());
        add(new JLabel("Participants"), BorderLayout.NORTH);
        add(new JScrollPane(box), BorderLayout.CENTER);
        host.participants.listen(this::update);
    }

    public void update(ArrayList<Participant> participants) {
        if (participants == null) return;
        box.removeAll();
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.0001;
        c.fill = GridBagConstraints.BOTH;
        for (int i = 0; i < participants.size(); i++) {
            c.gridy = i;
            Participant p = participants.get(i);
            c.gridx = 0;
            c.weightx = 0;
            JLabel pos = new JLabel("#" + p.place);
            pos.setFont(Fonts.spacemono.deriveFont(Font.BOLD, pos.getFont().getSize()));
            pos.setBorder(new EmptyBorder(2, 0, 2, 5));
            box.add(pos, c);

            c.gridx = 1;
            c.weightx = 1;
            JLabel name = new JLabel(p.name);
            name.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            name.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    String reason = JOptionPane.showInputDialog("Kick for reason?", "i dont like you");
                    if (reason != null && p.id != null)
                        host.getClient(UUID.fromString(p.id)).sendEvent(ContestHost.EVENT_SERVER_KICK, reason);
                }
            });
            box.add(name, c);
            c.weightx = 0;
            c.gridx = 2;
            box.add(new JLabel(String.valueOf(p.score)) {{
                setHorizontalAlignment(RIGHT);
                setFont(Fonts.opensans.deriveFont(Font.BOLD, getFont().getSize()));
            }}, c);
        }
        c.weighty = 1;
        c.gridy++;
        box.add(new JPanel(), c);
        revalidate();
        repaint();
    }
}
