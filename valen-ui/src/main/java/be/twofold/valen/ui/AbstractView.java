package be.twofold.valen.ui;

import be.twofold.valen.ui.util.*;

public abstract class AbstractView<TListener> implements View<TListener> {
    private final Listeners<TListener> listeners;

    protected AbstractView(Class<TListener> listenerClass) {
        listeners = new Listeners<>(listenerClass);
    }

    public Listeners<TListener> listeners() {
        return listeners;
    }

    @Override
    public void addListener(TListener listener) {
        listeners.addListener(listener);
    }

    @Override
    public void removeListener(TListener listener) {
        listeners.removeListener(listener);
    }
}
