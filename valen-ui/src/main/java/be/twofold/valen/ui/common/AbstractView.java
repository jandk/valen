package be.twofold.valen.ui.common;

import wtf.reversed.toolbox.util.*;

public abstract class AbstractView<T extends ViewListener> implements View<T> {
    private T listener;

    protected T getListener() {
        return listener;
    }

    @Override
    public final void setListener(T listener) {
        this.listener = Check.nonNull(listener, "listener");
    }
}
