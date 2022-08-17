package z.yun.contest.observable;

public interface Bindable<T> {
    T get();

    Bindable<T> listen(ChangeListener<T> listener);

    Bindable<T> stop(ChangeListener<T> listener);
}
