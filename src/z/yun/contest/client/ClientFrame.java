package z.yun.contest.client;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import z.yun.contest.ContestAppMenu;
import z.yun.contest.Fonts;
import z.yun.contest.Icons;
import z.yun.contest.Utils;
import z.yun.contest.data.Answer;
import z.yun.contest.data.Contest;
import z.yun.contest.data.Participant;
import z.yun.contest.observable.Bindings;
import z.yun.contest.observable.ReactiveLabel;
import z.yun.contest.server.ContestHost;
import z.yun.contest.server.ui.ContestPlayer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Objects;

import static z.yun.contest.client.ContestClient.EVENT_ANSWER;

public class ClientFrame extends JDialog {
    public final ContestClient client;
    private JComponent currentBody;

    public ClientFrame(Frame parent, String host, String name) {
        super(parent, "Client", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        setJMenuBar(new ContestAppMenu());
        Participant participant = new Participant(null, name);
        participant.score = 0;
        host = host.replace("tcp://", "http://").trim();
        client = new ContestClient(host, participant);
        add(createInfoHud(), BorderLayout.NORTH);
        refreshCenter(client.contest.get());
        add(createServerInfo(), BorderLayout.SOUTH);
        setPreferredSize(new Dimension(400, 400));
        client.contest.listen(this::refreshCenter);
        client.on(ContestHost.EVENT_SERVER_KICK, e -> {
            JOptionPane.showMessageDialog(ClientFrame.this, e[0], "Kicked", JOptionPane.ERROR_MESSAGE);
            dispose();
        });
        System.out.println(host);
        client.connect();
        pack();
        setLocationRelativeTo(parent);
    }

    public JComponent createInfoHud() {
        Box box = Box.createHorizontalBox();
        box.setBorder(new EmptyBorder(10, 20, 10, 20));
        box.add(new ReactiveLabel<String>(Bindings.map(client.participant, p -> "#" + (p.place))) {{
            setFont(Fonts.opensans.deriveFont(Font.BOLD, 12));
        }});
        box.add(Box.createHorizontalStrut(5));
        box.add(new ReactiveLabel<String>(Bindings.map(client.participant, p -> p.name)) {{
            setBorder(new TitledBorder("Name"));
        }});
        box.add(Box.createHorizontalStrut(5));
        box.add(new ReactiveLabel<String>(Bindings.map(client.participant, p -> String.format("%6d", p.score))) {{
            setFont(Fonts.spacemono.deriveFont(Font.BOLD, 12));
            setBorder(new TitledBorder("Score"));
        }});
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalStrut(5));
        box.add(new ReactiveLabel<>(Bindings.format(client.ping, "\uD83D\uDCF6: %dms")));
        box.add(Box.createHorizontalStrut(5));
        box.add(new ReactiveLabel<>(client.status));
        box.setPreferredSize(new Dimension(400, box.getPreferredSize().height));
        box.setBorder(new CompoundBorder(
                BorderFactory.createRaisedSoftBevelBorder(),
                new EmptyBorder(2, 10, 2, 10)
        ));
        return box;
    }

    public JComponent createServerInfo() {
        Box box = Box.createHorizontalBox();
        box.setBorder(new EmptyBorder(10, 20, 10, 20));
        box.add(new ReactiveLabel<>(Bindings.map(client.contest, c -> c.title)));
        box.add(Box.createHorizontalGlue());
        box.add(Box.createHorizontalStrut(15));
        box.add(new ReactiveLabel<>(Bindings.map(client.contest, c -> c.hostedBy)));
        box.add(Box.createHorizontalStrut(5));
        box.add(new ReactiveLabel<>(Bindings.map(client.contest, c -> {
            if (c.current_index == -1) return "Not started";
            else if (c.current_index == -2) return "Ended";
            return "#" + (c.current_index + 1);
        })));
        return box;
    }

    public void refreshCenter(Contest contest) {
        if (currentBody != null) remove(currentBody);
        if (contest == null) return;
        if (contest.current_index == -1) {
            add(currentBody = ContestPlayer.createTitlePage(contest), BorderLayout.CENTER);
        } else if (contest.current_index == -2) {
            add(currentBody = ending());
        } else if (Objects.requireNonNull(client.participant.get()).answers.length > contest.current_index) {
            add(currentBody = ContestPlayer.createQuestionPane(contest, contest.questions.get(contest.current_index), ClientFrame.this, null), BorderLayout.CENTER);
            Utils.setEnabled(currentBody, contest.acceptingAnswers && Objects.requireNonNull(client.participant.get()).answers[contest.current_index] == null);
        }
        revalidate();
        repaint();
    }

    private Box ending() {
        Box box = Box.createVerticalBox();
        box.add(Box.createVerticalGlue());
        Participant p = client.participant.get();
        if (p != null) {
            FlatSVGIcon icon = p.place == 1 ? Icons.GOLD_MEDAL : p.place == 2 ? Icons.SILVER_MEDAL : p.place == 3 ? Icons.BRONZE_MEDAL : null;
            if (icon != null) box.add(new JLabel(icon.derive(64, 64)) {{
                setAlignmentX(CENTER_ALIGNMENT);
            }});
            box.add(new JLabel("#" + p.place) {{
                setAlignmentX(CENTER_ALIGNMENT);
                setFont(Fonts.montserrat.deriveFont(Font.BOLD, 32));
            }});
            box.add(new JLabel(p.name) {{
                setAlignmentX(CENTER_ALIGNMENT);
                setFont(Fonts.opensans.deriveFont(Font.BOLD, 32));
            }});
            box.add(new JLabel(String.valueOf(p.score)) {{
                setAlignmentX(CENTER_ALIGNMENT);
                setFont(Fonts.spacemono.deriveFont(Font.BOLD, 24));
            }});
        }
        box.add(Box.createVerticalGlue());
        return box;
    }

    public void answer(Answer o) {
        if (!client.participant.isNull() && !client.contest.isNull())
            client.participant.get().answers[client.contest.get().current_index] = o;
        client.emit(EVENT_ANSWER, o);
        Utils.setEnabled(currentBody, false);
    }

    public void dispose() {
        super.dispose();
        client.disconnect();
    }

    public Answer getCurrentAnswer() {
        Participant participant = client.participant.get();
        Contest contest = client.contest.get();
        return contest != null && participant != null ? participant.answers[contest.current_index] : null;
    }
}
