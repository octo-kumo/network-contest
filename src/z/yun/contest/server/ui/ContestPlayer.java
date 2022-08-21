package z.yun.contest.server.ui;

import z.yun.contest.Fonts;
import z.yun.contest.data.Question;
import z.yun.contest.server.ContestHost;

import javax.swing.*;
import java.awt.*;

public class ContestPlayer extends JPanel {
    public ContestPlayer(ContestHost host) {
        setLayout(new BorderLayout());

    }

    public void createQuestionPane(Question question) {
        Box box = Box.createVerticalBox();
        JLabel title = new JLabel(question.title);
        title.setFont(Fonts.montserrat.deriveFont(24f));
        JLabel desc = new JLabel("<html>" + question.desc + "</html>");

        box.add(title);
        box.add(desc);
    }
}
