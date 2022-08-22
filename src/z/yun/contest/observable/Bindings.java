package z.yun.contest.observable;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Function;

public class Bindings {
    public static <T> Bindable<String> format(Bindable<T> observable, String format) {
        return map(observable, o -> String.format(format, o));
    }

    public static <T, U> Bindable<U> map(Bindable<T> observable, Function<T, U> mapper) {
        return new Bindable<U>() {
            final ArrayList<ChangeListener<U>> list = new ArrayList<>();

            @Nullable
            @Override
            public U get() {
                try {
                    return mapper.apply(observable.get());
                } catch (NullPointerException ignored) {
                    return null;
                }
            }

            @Override
            public Bindable<U> listen(ChangeListener<U> listener) {
                if (list.size() == 0) observable.listen(changed);
                synchronized (list) {
                    list.add(listener);
                }
                return this;
            }

            @Override
            public Bindable<U> stop(ChangeListener<U> listener) {
                synchronized (list) {
                    list.remove(listener);
                }
                if (list.size() == 0) observable.stop(changed);
                return this;
            }

            @Override
            public Bindable<U> ping() {
                changed.changed(observable.get());
                return this;
            }

            private final ChangeListener<T> changed = e -> {
                U v = mapper.apply(e);
                synchronized (list) {
                    list.forEach(p -> p.changed(v));
                }
            };
        };
    }
}
