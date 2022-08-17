package z.yun.contest.client;

import z.yun.contest.data.Participant;
import z.yun.contest.observable.Bindings;
import z.yun.contest.observable.ReactiveLabel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClientFrame extends JDialog {
    private final ContestClient client;
    private final Participant participant;

    public ClientFrame(Frame parent, String host, String name) {
        super(parent, "Client", false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        participant = new Participant(null, name);
        client = new ContestClient(host, participant);
        client.connect();
        add(createInfoHud(), BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(parent);
    }

    public JComponent createInfoHud() {
        Box box = Box.createHorizontalBox();
        box.setBorder(new EmptyBorder(10, 20, 10, 20));
        box.add(new JLabel(participant.name));
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalStrut(15));
        box.add(new ReactiveLabel<>(Bindings.format(client.ping, "\uD83D\uDCF6: %dms")));
        box.add(Box.createHorizontalStrut(5));
        box.add(new ReactiveLabel<>(client.status));
        return box;
    }

    public void dispose() {
        super.dispose();
        client.disconnect();
    }
}
