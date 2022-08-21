package z.yun.contest.server.ui;

import z.yun.contest.Fonts;
import z.yun.contest.Icons;
import z.yun.contest.Markdown;
import z.yun.contest.Utils;
import z.yun.contest.data.Question;
import z.yun.contest.server.ContestHost;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class ContestPlayer extends JPanel {
    private Box currentBody;
    private AbstractAction next, stop, exit;

    public ContestPlayer(ContestHost host) {
        setLayout(new BorderLayout());
        refreshCenter(host);
        add(createToolbar(host), BorderLayout.SOUTH);
    }

    private void refreshCenter(ContestHost host) {
        if (currentBody != null) remove(currentBody);

        if (host.contest.current_index == -1) {
            add(currentBody = createTitlePage(host), BorderLayout.CENTER);
        } else if (host.contest.current_index == -2) {

        } else
            add(currentBody = createQuestionPane(host, host.questions.get().get(host.contest.current_index)), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private Box createTitlePage(ContestHost host) {
        Box box = Box.createVerticalBox();
        JLabel title = new JLabel(host.contest.title);
        title.setFont(Fonts.opensans.deriveFont(28f));
        JLabel desc = new JLabel("<html>" + Markdown.parse(host.contest.description) + "</html>");
        desc.setFont(Fonts.opensans.deriveFont(14f));
        JLabel hosted = new JLabel("<html>Hosted by <b>" + host.contest.hostedBy + "</b></html>");
        box.add(Box.createVerticalStrut(10));
        box.add(title);
        box.add(desc);
        box.add(Box.createVerticalStrut(5));
        box.add(hosted);
        box.add(Box.createVerticalStrut(5));
        box.add(Box.createVerticalGlue());
        JLabel icon = new JLabel(new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/image/anime-anime-loading.gif"))));
        if (host.contest.image != null)
            Utils.downloadImage(host.contest.image, image -> {
                if (image != null)
                    icon.setIcon(new ImageIcon(image.getScaledInstance(
                            image.getWidth(null) * 256 / image.getHeight(null), 256, Image.SCALE_SMOOTH)));
            });
        box.add(icon);
        box.add(Box.createVerticalStrut(10));
        return box;
    }

    private JToolBar createToolbar(ContestHost host) {
        JToolBar bar = new JToolBar();
        bar.add(new AbstractAction("refresh", Icons.SYNC) {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshCenter(host);
            }
        });
        bar.add(next = new AbstractAction("next", Icons.PLAY) {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextQuestion(host);
            }
        });
        bar.add(stop = new AbstractAction("stop", Icons.STOP) {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAccepting(host);
            }
        });
        bar.add(exit = new AbstractAction("exit", Icons.POWER) {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopContest(host);
            }
        });
        stop.setEnabled(false);
        return bar;
    }

    private void stopContest(ContestHost host) {
        next.setEnabled(true);
        stop.setEnabled(false);
        host.contest.acceptingAnswers = false;
        host.contest.current_index = -1;
        refreshCenter(host);
        host.refresh();
    }

    private void stopAccepting(ContestHost host) {
        next.setEnabled(true);
        stop.setEnabled(false);
        host.contest.acceptingAnswers = false;
        host.refresh();
    }

    private void nextQuestion(ContestHost host) {
        if (host.contest.current_index == host.contest.questions.size() - 1) {
            endContest(host);
        } else {
            next.setEnabled(false);
            stop.setEnabled(true);
            host.contest.current_index++;
            host.contest.acceptingAnswers = true;
            refreshCenter(host);
            host.refresh();
        }
    }

    private void endContest(ContestHost host) {
        next.setEnabled(true);
        stop.setEnabled(false);
        host.contest.current_index = -2;
        host.contest.acceptingAnswers = false;
        refreshCenter(host);
        host.refresh();
    }

    public Box createQuestionPane(ContestHost host, Question question) {
        Box box = Box.createVerticalBox();
        box.setBorder(new CompoundBorder(new TitledBorder(null, String.format("#%d/%d", question.index + 1, host.contest.questions.size()), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, Fonts.spacemono.deriveFont(14f)), new EmptyBorder(10, 10, 10, 10)));
        JLabel title = new JLabel(question.title);
        title.setFont(Fonts.opensans.deriveFont(24f));
        title.setMaximumSize(new Dimension(Integer.MAX_VALUE, title.getPreferredSize().height));
        title.setAlignmentX(CENTER_ALIGNMENT);
        JLabel desc = new JLabel("<html>" + Markdown.parse(question.desc) + "</html>");
        desc.setMaximumSize(new Dimension(Integer.MAX_VALUE, desc.getPreferredSize().height));
        desc.setAlignmentX(CENTER_ALIGNMENT);

        box.add(title);
        box.add(desc);
        box.add(Box.createVerticalStrut(15));
        box.add(createChoices(question));
        box.add(Box.createVerticalGlue());
        return box;
    }

    private Box createChoices(Question question) {
        Box box = Box.createVerticalBox();
        for (int i = 0; i < question.options.length; i++) {
            box.add(createChoiceBar(question, question.options[i], i));
        }
        box.revalidate();
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, box.getPreferredSize().height));
        box.setBorder(new TitledBorder("Choice(s)"));
        return box;
    }

    private Box createChoiceBar(Question question, String option, int index) {
        Box box = Box.createHorizontalBox();
        box.add(new JLabel(QuestionHolder.ALPHABETS[index] + ")") {{
            setFont(Fonts.spacemono.deriveFont(10f));
        }});
        box.add(Box.createHorizontalStrut(5));
        box.add(new JLabel("<html>" + Markdown.parse(option) + "</html>") {{
            setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        }});
        box.add(question.type == Question.Type.MCQ ? new JRadioButton() {{
            setEnabled(false);
        }} : new JCheckBox() {{
            setEnabled(false);
        }});
        return box;
    }
}
