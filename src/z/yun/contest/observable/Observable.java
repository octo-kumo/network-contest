package z.yun.contest.observable;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Observable<T> implements Bindable<T> {
    private @Nullable T value;
    private final ArrayList<ChangeListener<T>> listeners = new ArrayList<>();

    public Observable() {
        this(null);
    }

    public Observable(@Nullable T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public Observable<T> set(T value) {
        this.value = value;
        return ping();
    }

    public Observable<T> ping() {
        listeners.forEach(l -> {
            try {
                l.changed(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return this;
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
