package z.yun.contest.server;

import z.yun.contest.ContestApp;
import z.yun.contest.data.Contest;
import z.yun.contest.data.Question;
import z.yun.contest.server.ui.ContestPlayer;
import z.yun.contest.server.ui.ContestSetup;
import z.yun.contest.server.ui.ParticipantList;
import z.yun.contest.server.ui.QuestionList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ServerFrame extends JDialog {

    private final ContestHost host;

    public ServerFrame(ContestApp contestApp, String text, int port) {
        super(contestApp, "Server", true);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Contest contest = new Contest(text, "Yggdrasil: The World Tree, perhaps the world's largest network ever described in a legend, colossal tree which supports the heavens, thereby connecting the heavens, the terrestrial world, and, through its roots, the underworld.", "yun");
        contest.questions.add(new Question("Simple??", "What is this **module**?",
                new String[]{"CS6132", "CS6131", "CS6111", "CS6432"}, 0));
        contest.questions.add(new Question("Topology", "How many cable links are required for a **mesh** topology?",
                new String[]{"`n!`", "`n(n-1)/2`", "`n(n+1)`", "`n`"}, 1));
        contest.questions.add(new Question("Transmission", "Which of the following are guided transmission?",
                new String[]{"coaxial cable", "fiber", "radio", "infrared"}, new boolean[]{true, true, false, false}));
        contest.questions.add(new Question("Topology", "What is the Hamming distance between 0001 and 0010?",
                "2"));
        contest.image = "https://res.cloudinary.com/chatboxzy/image/upload/c_scale,w_250/v1661168401/susco.png";
        contest.revalidate();
        host = new ContestHost(contest, port);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab("Setup", new ContestSetup(host));
        tabbedPane.addTab("Questions", new QuestionList(host));
        tabbedPane.addTab("Player", new ContestPlayer(host));
        tabbedPane.setPreferredSize(new Dimension(800, 600));
        add(tabbedPane, BorderLayout.CENTER);
        add(new ParticipantList(host), BorderLayout.WEST);
        pack();
        setLocationRelativeTo(null);
        try {
            host.start();
        } catch (Exception e) {
            System.out.println("Error");
            e.printStackTrace();
            host.stop();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("fail");
        }
    }

    public void dispose() {
        host.stop();
        super.dispose();
    }
}
