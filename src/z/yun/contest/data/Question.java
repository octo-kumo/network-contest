package z.yun.contest.data;

import org.jetbrains.annotations.Nullable;

public class Question {
    public static void addChoice(Question question, String choice) {
        if (question.correct == null) question.correct = new boolean[]{};
        if (question.options == null) question.options = new String[]{};
        revalidate(question);
        boolean[] c = new boolean[question.options.length + 1];
        String[] nopt = new String[question.options.length + 1];
        System.arraycopy(question.options, 0, nopt, 0, question.options.length);
        System.arraycopy(question.correct, 0, c, 0, question.correct.length);
        nopt[question.options.length] = choice;
        question.options = nopt;
        question.correct = c;
    }

    public static void removeChoice(Question question, int index) {
        if (question.correct == null) question.correct = new boolean[]{};
        if (question.options == null) question.options = new String[]{};
        revalidate(question);
        boolean[] c = new boolean[question.correct.length - 1];
        String[] nopt = new String[question.options.length - 1];
        System.arraycopy(question.options, 0, nopt, 0, index);
        System.arraycopy(question.correct, 0, c, 0, index);
        System.arraycopy(question.options, index + 1, nopt, index, question.options.length - index - 1);
        System.arraycopy(question.correct, index + 1, c, index, question.correct.length - index - 1);
        question.options = nopt;
        question.correct = c;
    }

    public static void revalidate(Question question) {
        if (question.correct == null) question.correct = new boolean[]{};
        if (question.options == null) question.options = new String[]{};
        if (question.options.length != question.correct.length) {
            boolean[] c = new boolean[question.options.length];
            System.arraycopy(question.correct, 0, c, 0, question.correct.length);
            question.correct = c;
        }
    }

    public enum Type {
        MCQ, MRQ, TEXT
    }

    public Type type;
    public int index;
    public String title;
    public String desc;

    public String @Nullable [] options;
    public boolean @Nullable [] correct;
    public String answer;

    public Question() {
        this.type = Type.MCQ;
        this.index = -1;
        this.title = "";
        this.desc = "";
        options = new String[]{};
        correct = new boolean[]{};
        this.answer = "";
    }

    public Question(Type type, String title, String desc, String @Nullable [] options, boolean @Nullable [] correct, String answer) {
        this.type = type;
        this.title = title;
        this.desc = desc;
        this.options = new String[]{};
        this.options = options;
        this.correct = new boolean[]{};
        this.correct = correct;
        this.answer = answer;
        revalidate(this);
    }

    public Question(String title, String desc, String[] options, int correct) {
        this(Type.MCQ, title, desc, options, genCorrect(correct, options.length), null);
    }

    public Question(String title, String desc, String[] options, boolean[] correct) {
        this(Type.MRQ, title, desc, options, correct, null);
    }

    public Question(String title, String desc, String answer) {
        this(Type.TEXT, title, desc, new String[]{}, new boolean[]{}, answer);
    }

    private static boolean[] genCorrect(int correct, int length) {
        boolean[] c = new boolean[length];
        c[correct] = true;
        return c;
    }
}
