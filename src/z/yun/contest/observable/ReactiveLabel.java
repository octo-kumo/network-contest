package z.yun.contest.observable;

import javax.swing.*;

public class ReactiveLabel<T> extends JLabel implements ChangeListener<T> {
    private final Bindable<T> observable;

    public ReactiveLabel(Bindable<T> observable) {
        super(observable.get().toString());
        this.observable = observable;
    }

    public void addNotify() {
        observable.listen(this);
    }

    public void removeNotify() {
        observable.stop(this);
    }

    @Override
    public void changed(T n) {
        setText(n.toString());
        revalidate();
        repaint();
    }
}
