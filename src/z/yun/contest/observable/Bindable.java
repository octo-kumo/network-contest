package z.yun.contest.observable;

import java.util.Optional;

public interface Bindable<T> {
    T get();

    Bindable<T> listen(ChangeListener<T> listener);

    Bindable<T> stop(ChangeListener<T> listener);

    default Optional<T> getAsOptional() {
        return Optional.ofNullable(get());
    }
}
