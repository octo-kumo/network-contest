package z.yun.contest.observable;

import java.util.ArrayList;

public class Observable<T> implements Bindable<T> {
    private T value;
    private final ArrayList<ChangeListener<T>> listeners = new ArrayList<>();

    public Observable() {
        this(null);
    }

    public Observable(T value) {
        this.value = value;
    }

    public Observable(T value, ChangeListener<T> listener) {
        this(value);
        listen(listener);
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        listeners.forEach(l -> l.changed(value));
        this.value = value;
    }

    public Observable<T> listen(ChangeListener<T> listener) {
        listeners.add(listener);
        return this;
    }

    public Observable<T> stop(ChangeListener<T> listener) {
        listeners.remove(listener);
        return this;
    }
}
