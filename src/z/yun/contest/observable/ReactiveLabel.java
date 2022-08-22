package z.yun.contest.observable;

import javax.swing.*;

public class ReactiveLabel<T> extends JLabel implements ChangeListener<T> {
    private final Bindable<T> observable;

    public ReactiveLabel(Bindable<T> observable) {
        super(observable.isNull() ? "" : observable.get().toString());
        this.observable = observable;
    }

    public void addNotify() {
        SwingUtilities.invokeLater(() -> observable.listen(this));
    }

    public void removeNotify() {
        SwingUtilities.invokeLater(() -> observable.stop(this));
    }

    @Override
    public void changed(T n) {
        setText(n.toString());
        getRootPane().repaint();
    }
}
