package z.yun.contest.data;

public class Question {
    public enum Type {
        MCQ, MRQ, TEXT
    }

    public Type type;
    public int index;
    public String title;
    public String desc;

    public String image;
    public String[] options;

    public Object answer;

    public int getCorrectChoice() {
        return (int) answer;
    }

    public int[] getCorrectChoices() {
        return (int[]) answer;
    }

    public String getCorrectAnswer() {
        return (String) answer;
    }
}
