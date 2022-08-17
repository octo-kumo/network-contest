package z.yun.contest.observable;

import java.util.HashMap;
import java.util.function.Function;

public class Bindings {
    public static <T> Bindable<String> format(Observable<T> observable, String format) {
        return new Bindable<String>() {
            final HashMap<ChangeListener<String>, ChangeListener<T>> map = new HashMap<>();

            @Override
            public String get() {
                return String.format(format, observable.get());
            }

            @Override
            public Bindable<String> listen(ChangeListener<String> listener) {
                ChangeListener<T> l = e -> listener.changed(String.format(format, e));
                map.put(listener, l);
                observable.listen(l);
                return this;
            }

            @Override
            public Bindable<String> stop(ChangeListener<String> listener) {
                observable.stop(map.remove(listener));
                return this;
            }
        };
    }

    public static <T, U> Bindable<U> map(Observable<T> observable, Function<T, U> mapper) {
        return new Bindable<U>() {
            final HashMap<ChangeListener<U>, ChangeListener<T>> map = new HashMap<>();

            @Override
            public U get() {
                return mapper.apply(observable.get());
            }

            @Override
            public Bindable<U> listen(ChangeListener<U> listener) {
                ChangeListener<T> l = e -> listener.changed(mapper.apply(e));
                map.put(listener, l);
                observable.listen(l);
                return this;
            }

            @Override
            public Bindable<U> stop(ChangeListener<U> listener) {
                observable.stop(map.remove(listener));
                return this;
            }
        };
    }
}
