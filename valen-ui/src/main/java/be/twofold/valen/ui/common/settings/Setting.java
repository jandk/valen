package be.twofold.valen.ui.common.settings;

import java.util.*;
import java.util.concurrent.*;

public final class Setting<T> {
    private final Set<ChangeListener<T>> listeners = new CopyOnWriteArraySet<>();

    private T value;

    public Setting() {
        this(null);
    }

    public Setting(T value) {
        this.value = value;
    }

    public Optional<T> get() {
        return Optional.ofNullable(value);
    }

    public void set(final T value) {
        var oldValue = this.value;
        this.value = value;
        listeners.forEach(l -> l.changed(oldValue, value));
    }

    public void addListener(ChangeListener<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(ChangeListener<T> listener) {
        listeners.remove(listener);
    }

    @FunctionalInterface
    public interface ChangeListener<T> {
        void changed(T oldValue, T newValue);
    }
}
