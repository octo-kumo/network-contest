package z.yun.contest.server;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import z.yun.contest.Icons;
import z.yun.contest.data.Question;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class QuestionList extends JPanel {

    private final Box box;

    public QuestionList(final ContestHost host) {
        setLayout(new BorderLayout());
        box = Box.createVerticalBox();

        setPreferredSize(new Dimension(300, 400));
        add(new JScrollPane(box) {{
            getVerticalScrollBar().setUnitIncrement(8);
        }}, BorderLayout.CENTER);
        add(createToolbar(host), BorderLayout.SOUTH);
        host.questions.listen(e -> this.update(host, e));
        this.update(host, host.questions.get());
    }

    private JToolBar createToolbar(ContestHost host) {
        JToolBar bar = new JToolBar();
        bar.add(new AbstractAction("add",
                new FlatSVGIcon(Icons.PLUS)) {
            @Override
            public void actionPerformed(ActionEvent e) {
                host.contest.questions.add(new Question());
                host.contest.revalidate();
                host.questions.set(host.contest.questions);
            }
        });
        return bar;
    }

    public void update(ContestHost host, ArrayList<Question> questions) {
        if (questions == null) return;
        box.removeAll();
        questions.forEach(p -> box.add(new QuestionHolder(host, p)));
        box.add(Box.createVerticalGlue());
        revalidate();
        repaint();
    }
}
