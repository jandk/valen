package be.twofold.valen.ui.common;

import be.twofold.valen.core.util.*;

public abstract class AbstractPresenter<T> {
    private final T view;

    public AbstractPresenter(T view) {
        this.view = Check.notNull(view, "view");
    }

    public final T getView() {
        return view;
    }
}
