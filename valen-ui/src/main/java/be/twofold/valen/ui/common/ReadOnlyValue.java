package be.twofold.valen.ui.common;

import java.util.function.*;

public interface ReadOnlyValue<T> {
    T get();

    Subscription subscribe(Consumer<? super T> listener);
}
