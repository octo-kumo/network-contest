package z.yun.contest.observable;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface Bindable<T> {
    @Nullable T get();

    Bindable<T> listen(ChangeListener<T> listener);

    Bindable<T> stop(ChangeListener<T> listener);

    Bindable<T> ping();

    default Optional<T> getAsOptional() {
        return Optional.ofNullable(get());
    }

    default boolean isNull() {
        return get() == null;
    }

    default T or(T other) {
        T t = get();
        return t == null ? other : t;
    }
}
