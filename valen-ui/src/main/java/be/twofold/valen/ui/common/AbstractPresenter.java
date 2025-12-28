package be.twofold.valen.ui.common;

import wtf.reversed.toolbox.util.*;

public abstract class AbstractPresenter<T> {
    private final T view;

    public AbstractPresenter(T view) {
        this.view = Check.nonNull(view, "view");
    }

    public final T getView() {
        return view;
    }
}
