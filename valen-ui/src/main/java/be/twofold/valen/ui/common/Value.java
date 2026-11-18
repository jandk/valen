package be.twofold.valen.ui.common;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * An observable value. Write from one thread only, listeners run on that thread.
 */
public final class Value<T> implements ReadOnlyValue<T> {
    private final List<Consumer<? super T>> listeners = new CopyOnWriteArrayList<>();
    private volatile T value;

    @Override
    public T get() {
        return value;
    }

    public void set(T value) {
        if (Objects.equals(value, this.value)) {
            return;
        }
        this.value = value;
        listeners.forEach(listener -> listener.accept(value));
    }

    @Override
    public Subscription subscribe(Consumer<? super T> listener) {
        listeners.add(listener);
        listener.accept(value);
        return () -> listeners.remove(listener);
    }
}
