package z.yun.contest.server.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import z.yun.contest.Fonts;
import z.yun.contest.Icons;
import z.yun.contest.Markdown;
import z.yun.contest.Utils;
import z.yun.contest.client.ClientFrame;
import z.yun.contest.data.Answer;
import z.yun.contest.data.Contest;
import z.yun.contest.data.Participant;
import z.yun.contest.data.Question;
import z.yun.contest.observable.Bindings;
import z.yun.contest.observable.ReactiveLabel;
import z.yun.contest.server.ContestHost;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ContestPlayer extends JPanel implements ActionListener {
    private static final Color CORRECT_COLOR = new Color(0x4400ff00, true);
    private static final Color INCORRECT_COLOR = new Color(0x44ff0000, true);
    public static Logger LOGGER = LoggerFactory.getLogger(ContestPlayer.class);
    private JComponent currentBody;
    private AbstractAction next, stop, exit;
    private final JProgressBar questionsBar;

    public ContestPlayer(ContestHost host) {
        setLayout(new BorderLayout());
        add(questionsBar = new JProgressBar(), BorderLayout.NORTH);
        refreshCenter(host);
        add(createToolbar(host), BorderLayout.SOUTH);
        host.answerCount.listen(a -> {
            if (a == Objects.requireNonNull(host.participants.get()).size()) stopAccepting(host);
        });
        new Timer(16, this).start();
    }

    private void refreshCenter(ContestHost host) {
        if (currentBody != null) remove(currentBody);

        if (host.contest.current_index == -1) {
            add(currentBody = createTitlePage(host.contest), BorderLayout.CENTER);
        } else if (host.contest.current_index == -2) {
            add(currentBody = leaderBoard(host.contest), BorderLayout.CENTER);
        } else
            add(currentBody = createQuestionPane(host.contest, host.questions.get().get(host.contest.current_index), null, host), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public static Box createTitlePage(Contest contest) {
        Box box = Box.createVerticalBox();
        JLabel title = new JLabel(contest.title);
        title.setFont(Fonts.opensans.deriveFont(28f));
        JLabel desc = new JLabel("<html>" + Markdown.parse(contest.description) + "</html>");
        desc.setFont(Fonts.opensans.deriveFont(14f));
        JLabel hosted = new JLabel("<html>Hosted by <b>" + contest.hostedBy + "</b></html>");
        box.add(title);
        box.add(desc);
        box.add(Box.createVerticalStrut(5));
        box.add(hosted);
        box.add(Box.createVerticalStrut(5));
        box.add(Box.createVerticalGlue());
        JLabel icon = new JLabel(new ImageIcon(Objects.requireNonNull(ContestPlayer.class.getResource("/image/anime-anime-loading.gif"))));
        if (contest.image != null) Utils.downloadImage(contest.image, image -> {
            if (image != null)
                icon.setIcon(new ImageIcon(image.getScaledInstance(image.getWidth(null) * 256 / image.getHeight(null), 256, Image.SCALE_SMOOTH)));
        });
        box.add(icon);
        box.setBorder(new EmptyBorder(10, 10, 10, 10));
        return box;
    }

    public static JComponent leaderBoard(Contest contest) {
        Box box = Box.createVerticalBox();
        contest.updateScore();

        box.add(new JLabel("Leaderboard") {{
            setFont(Fonts.opensans.deriveFont(Font.BOLD, 32f));
        }});
        contest.updateScore();
        for (Participant e : contest.participants) {
            box.add(getParticipantLabel(e));
            box.add(new JSeparator());
        }
        box.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return new JScrollPane(box);
    }

    @NotNull
    public static JLabel getParticipantLabel(Participant e) {
        return new JLabel("<html><pre>#" + e.place + " / " + e.score + "</pre> " + e.name + "</html>", e.place == 1 ? Icons.GOLD_MEDAL : e.place == 2 ? Icons.SILVER_MEDAL : e.place == 3 ? Icons.BRONZE_MEDAL : null, JLabel.LEFT) {{
            setFont(Fonts.opensans.deriveFont(Font.BOLD, e.place < 4 ? 18f : 12f));
        }};
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
        host.clearAnswers();
        host.refresh();
        LOGGER.info("contest stopped");
    }

    private void stopAccepting(ContestHost host) {
        next.setEnabled(true);
        stop.setEnabled(false);
        host.contest.acceptingAnswers = false;
        host.contest.updateScore();
        host.participants.ping();
        refreshCenter(host);
        host.refresh();
        LOGGER.info("question #" + host.contest.current_index + " ended");
    }

    private void nextQuestion(ContestHost host) {
        if (host.contest.current_index == -2) {
            host.contest.current_index = -1;
            host.contest.acceptingAnswers = false;
            host.clearAnswers();
            host.refresh();
            refreshCenter(host);
        } else if (host.contest.current_index == host.contest.questions.size() - 1) {
            endContest(host);
        } else {
            next.setEnabled(false);
            stop.setEnabled(true);
            host.contest.current_index++;
            host.contest.acceptingAnswers = true;
            questionsBar.setMinimum(0);
            questionsBar.setMaximum(host.contest.questions.size());
            questionsBar.setValue(host.contest.current_index);
            refreshCenter(host);
            host.refresh();
            LOGGER.info("now onto question #" + host.contest.current_index);
        }
    }

    private void endContest(ContestHost host) {
        next.setEnabled(true);
        stop.setEnabled(false);
        host.contest.current_index = -2;
        host.contest.acceptingAnswers = false;
        questionsBar.setMaximum(host.contest.questions.size());
        questionsBar.setValue(host.contest.questions.size());
        refreshCenter(host);
        host.refresh();
    }

    public static Box createQuestionPane(Contest contest, Question question, ClientFrame client, @Nullable ContestHost host) {
        Box box = Box.createVerticalBox();
        box.setBorder(new CompoundBorder(new TitledBorder(null, String.format("#%d/%d", question.index + 1, contest.questions.size()), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, Fonts.spacemono.deriveFont(14f)), new EmptyBorder(10, 10, 10, 10)));
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
        boolean onClient = client != null;
        boolean onServer = host != null;
        if (onClient == onServer) throw new RuntimeException("Client and server side code should be separate!");

        boolean[] userAnswer = question.options != null && onClient ?
                !contest.acceptingAnswers && client.getCurrentAnswer() != null ?
                        client.getCurrentAnswer().choice :
                        new boolean[question.options.length] : new boolean[0];

        JTextField answerField = new JTextField();
        if (question.type == Question.Type.TEXT) {
            box.add(answerField);
            if (!contest.acceptingAnswers) {
                answerField.setText(onClient ? client.getCurrentAnswer() == null ? "" : client.getCurrentAnswer().answer : question.answer);
                answerField.setEditable(false);
                if (onClient)
                    answerField.setBackground(Objects.equals(client.getCurrentAnswer() == null ? "" : client.getCurrentAnswer().answer, question.answer) ? CORRECT_COLOR : INCORRECT_COLOR);
            } else if (!onClient) answerField.setEditable(false);
        } else box.add(createChoices(contest, question, userAnswer, question.correct, onClient));
        box.add(Box.createVerticalStrut(15));
        if (onClient) box.add(new JButton("Submit") {{
            addActionListener(e -> client.answer(new Answer(question.type,
                    question.type == Question.Type.TEXT ? answerField.getText() : userAnswer)));
        }});
        else {
            if (host.contest.acceptingAnswers) box.add(createResponseCount(host));
            else box.add(createResponseSummary(host));
        }
        box.add(Box.createVerticalGlue());
        return box;
    }

    private static Box createChoices(Contest contest, Question question, boolean[] answer, boolean[] correctAnswer, boolean onClient) {
        Box box = Box.createVerticalBox();
        if (question.options == null) return box;
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i < question.options.length; i++) {
            box.add(createChoiceBar(contest, question, answer, correctAnswer, group, i, onClient));
        }
        box.revalidate();
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, box.getPreferredSize().height));
        box.setBorder(new TitledBorder("Choice(s)"));
        return box;
    }

    private static Box createChoiceBar(Contest contest, Question question, boolean[] answer, boolean[] correctAnswer, ButtonGroup group, int index, boolean onClient) {
        Box box = Box.createHorizontalBox();
        box.add(new JLabel(QuestionHolder.ALPHABETS[index] + ")") {{
            setFont(Fonts.spacemono.deriveFont(10f));
        }});
        box.add(Box.createHorizontalStrut(5));
        if (question.options != null)
            box.add(new JLabel("<html>" + Markdown.parse(question.options[index]) + "</html>") {{
                setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
            }});
        JToggleButton button;
        box.add(button = question.type == Question.Type.MCQ ? new JRadioButton() : new JCheckBox());
        button.setEnabled(contest.acceptingAnswers && onClient);
        if (!contest.acceptingAnswers) {
            if (!onClient) {
                button.setSelected(correctAnswer[index]);
                if (correctAnswer[index]) {
                    box.setBackground(CORRECT_COLOR);
                    box.setOpaque(true);
                }
            } else {
                button.setSelected(answer[index]);
                if (answer[index] || correctAnswer[index]) {
                    box.setBackground(answer[index] == correctAnswer[index] ? CORRECT_COLOR : INCORRECT_COLOR);
                    box.setOpaque(true);
                }
            }
        }
        if (onClient) button.addItemListener(e -> answer[index] = e.getStateChange() == ItemEvent.SELECTED);
        if (question.type == Question.Type.MCQ) group.add(button);
        return box;
    }

    private static Box createResponseCount(ContestHost host) {
        Box box = Box.createHorizontalBox();
        box.add(new ReactiveLabel<String>(Bindings.format(host.answerCount, "<html>Received <b>%d</b> responses</html>")) {{
            setIcon(Icons.LOADING);
        }});
        return box;
    }

    private static JPanel createResponseSummary(ContestHost host) {
        JPanel panel = new JPanel();
        panel.setBorder(new CompoundBorder(
                new TitledBorder("Summary"),
                new EmptyBorder(10, 25, 10, 25)
        ));
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        HashMap<String, Answer> answers = Objects.requireNonNull(host.answers.get())[host.contest.current_index];
        Question question = host.contest.questions.get(host.contest.current_index);
        System.out.println("\n\n=============\ncreateResponseSummary");
        System.out.println(question.type);
        System.out.println(Arrays.toString(question.options));
        if (question.type == Question.Type.TEXT) {
            Map<String, Integer> collect = answers.values().stream().map(a -> a.answer).collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(e -> 1)));
            List<Map.Entry<String, Integer>> sorted = collect.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).collect(Collectors.toList());
            int total = collect.values().stream().reduce(0, Integer::sum);
            for (int i = 0; i < sorted.size(); i++) {
                c.gridx = 0;
                c.gridy = i;
                c.weightx = 0;
                panel.add(new JLabel(sorted.get(i).getKey()) {{
                    setFont(Fonts.opensans.deriveFont(Font.BOLD, 12f));
                    setBorder(new EmptyBorder(5, 5, 5, 5));
                }}, c);

                c.gridx = 1;
                c.weightx = 1;
                JProgressBar bar = new JProgressBar(0, total);
                bar.setValue(sorted.get(i).getValue());
                bar.setStringPainted(true);
                bar.setPreferredSize(new Dimension(Integer.MAX_VALUE, bar.getPreferredSize().height));
                panel.add(bar, c);

                c.gridx = 2;
                c.weightx = 0;
                panel.add(new JLabel(String.valueOf(sorted.get(i).getValue()), Icons.CLOSE.derive(8, 8), SwingConstants.LEFT) {{
                    setBorder(new EmptyBorder(5, 5, 5, 5));
                }}, c);
            }
        } else if (question.options != null) {
            int[] freq = new int[question.options.length];
            System.out.println(answers.entrySet().stream().map(e -> e.getKey() + ": " + e.getValue()).collect(Collectors.joining("\n")));
            answers.values().stream().filter(a -> a.type != Question.Type.TEXT).forEach(a -> {
                for (int i = 0; i < Math.min(a.choice.length, question.options.length); i++)
                    freq[i] += a.choice[i] ? 1 : 0;
            });
            for (int i = 0; i < question.options.length; i++) {
                c.gridx = 0;
                c.gridy = i;
                c.weightx = 0;
                panel.add(new JLabel("<html>" + Markdown.parse(question.options[i]) + "</html>") {{
                    setBorder(new EmptyBorder(5, 5, 5, 5));
                }}, c);

                c.gridx = 1;
                c.weightx = 1;
                JProgressBar bar = new JProgressBar(0, answers.size());
                bar.setValue(freq[i]);
                bar.setStringPainted(true);
                bar.setPreferredSize(new Dimension(Integer.MAX_VALUE, bar.getPreferredSize().height));
//                bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
                panel.add(bar, c);

                c.gridx = 2;
                c.weightx = 0;
                panel.add(new JLabel(String.valueOf(freq[i]), Icons.CLOSE.derive(8, 8), SwingConstants.LEFT) {{
                    setBorder(new EmptyBorder(5, 5, 5, 5));
                }}, c);
            }
        }

        System.out.println("=============\n");
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentBody != null) currentBody.repaint();
    }
}
