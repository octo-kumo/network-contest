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
        super(contestApp, "Server", false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        Contest contest = new Contest(text, "Yggdrasil: The World Tree, perhaps the world's largest network ever described in a legend, colossal tree which supports the heavens, thereby connecting the heavens, the terrestrial world, and, through its roots, the underworld.", "yun");
        contest.questions.add(new Question(Question.Type.MCQ, "Test", "Lorem blah blab", null, new String[]{"a", "b (mememe)", "c", "d"}, new boolean[]{}, null));
        contest.questions.add(new Question(Question.Type.MRQ, "Test", "Lorem blah blab", null, new String[]{"a", "b (mememe)", "c", "d"}, new boolean[]{}, null));
        contest.questions.add(new Question(Question.Type.MRQ, "Test", "Lorem blah blab", null, new String[]{"a", "b (mememe)", "c", "d"}, new boolean[]{}, null));
        contest.questions.add(new Question(Question.Type.TEXT, "Test", "Lorem blah blab", null, null, null, null));
        contest.revalidate();
        host = new ContestHost(contest, port);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        tabbedPane.addTab("Setup", new ContestSetup(host));
        tabbedPane.addTab("Questions", new QuestionList(host));
        tabbedPane.addTab("Player", new ContestPlayer(host));
        add(tabbedPane, BorderLayout.CENTER);
        add(new ParticipantList(host), BorderLayout.WEST);

        pack();
        setLocationRelativeTo(null);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible)
            try {
                host.start();
            } catch (Exception e) {
                System.out.println("Error");
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        else host.stop();
    }

    public void dispose() {
        super.dispose();
        host.stop();
    }
}
