package z.yun.contest.server.ui;

import z.yun.contest.Fonts;
import z.yun.contest.Icons;
import z.yun.contest.Utils;
import z.yun.contest.data.Question;
import z.yun.contest.server.ContestHost;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

public class QuestionHolder extends JPanel {
    private final ButtonGroup buttonGroup;
    public static final char[] ALPHABETS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private JComponent body;

    public QuestionHolder(ContestHost host, Question question) {
        this.buttonGroup = new ButtonGroup();
        rebuild(host, question);
    }

    private JComponent createBody(ContestHost host, Question question) {
        Box box = Box.createVerticalBox();
        box.add(new JTextArea(question.desc, 2, 10) {{
            setBorder(new TitledBorder("Description"));
            Utils.addChangeListener(this, e -> question.desc = getText());
        }});
        box.add(new JSeparator());
        if (question.type == Question.Type.TEXT) {
            JTextField answerField = new JTextField(question.answer);
            Utils.addChangeListener(answerField, e -> question.answer = answerField.getText());
            answerField.setMaximumSize(new Dimension(Integer.MAX_VALUE, answerField.getPreferredSize().height));
            box.add(answerField);
        } else {
            Box answers = Box.createVerticalBox();
            answers.setBorder(new TitledBorder("Choice(s)"));
            updateAnswers(answers, question);
            box.add(answers);
            box.add(new JToolBar() {{
                add(new AbstractAction("+", Icons.PLUS.derive(12, 12)) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Question.addChoice(question, "");
                        updateAnswers(answers, question);
                    }
                });
                addSeparator();
                add(new AbstractAction("x", Icons.CLOSE.derive(12, 12)) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (JOptionPane.showConfirmDialog(null, "Delete question?") == JOptionPane.YES_OPTION) {
                            host.contest.questions.remove(question);
                            host.contest.revalidate();
                            host.questions.set(host.contest.questions);
                        }
                    }
                });
            }});
        }
        box.setVisible(body != null && body.isVisible());
        return box;
    }

    private void updateAnswers(Box answers, Question question) {
        answers.removeAll();
        for (int i = 0, optionsLength = question.options.length; i < optionsLength; i++)
            answers.add(createChoiceBar(answers, question, question.options[i], i));
        answers.revalidate();
        answers.repaint();
    }

    private Box createChoiceBar(Box answers, Question question, String option, int index) {
        Box box = Box.createHorizontalBox();
        box.add(new JLabel(ALPHABETS[index] + ")") {{
            setFont(Fonts.spacemono.deriveFont(10f));
        }});
        box.add(Box.createHorizontalStrut(5));
        JTextField field = new JTextField(option, 10);
        Utils.addChangeListener(field, e -> question.options[index] = field.getText());
        box.add(field);
        JToggleButton button;
        if (question.type == Question.Type.MCQ) buttonGroup.add(button = new JRadioButton());
        else button = new JCheckBox();
        button.setSelected(question.correct[index]);
        button.addItemListener(e -> question.correct[index] = e.getStateChange() == ItemEvent.SELECTED);
        box.add(button);
        JButton close = new JButton(Icons.CLOSE.derive(12, 12));
        close.putClientProperty("JButton.buttonType", "toolBarButton");
        close.addActionListener(e -> {
            Question.removeChoice(question, index);
            updateAnswers(answers, question);
        });
        box.add(close);
        return box;
    }

    private JComponent createTopBar(ContestHost host, Question question) {
        Box box = Box.createHorizontalBox();
        box.add(new JTextField(question.title, 10) {{
            setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
            Utils.addChangeListener(this, e -> question.title = getText());
        }});
        box.add(Box.createHorizontalGlue());
        JComboBox<Question.Type> type = new JComboBox<>(Question.Type.values());
        type.getModel().setSelectedItem(question.type);
        type.addActionListener(e -> {
            question.type = (Question.Type) type.getModel().getSelectedItem();
            rebuild(host, question);
        });
        box.add(type);
        box.add(new JToggleButton("Edit") {{
            addItemListener(e -> body.setVisible(e.getStateChange() == ItemEvent.SELECTED));
            setMaximumSize(new Dimension(getPreferredSize().width, Integer.MAX_VALUE));
        }});
        return box;
    }

    private void rebuild(ContestHost host, Question question) {
        removeAll();
        setLayout(new BorderLayout());
        setBorder(new TitledBorder(null, "#" + (question.index + 1), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, Fonts.spacemono.deriveFont(14f)));
        add(createTopBar(host, question), BorderLayout.NORTH);
        add(body = createBody(host, question), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
