package be.twofold.valen.ui;

import be.twofold.valen.core.util.*;

public abstract class AbstractPresenter<T extends View<?>> {
    private final T view;

    protected AbstractPresenter(T view) {
        this.view = Check.notNull(view);
    }

    public T getView() {
        return view;
    }
}
