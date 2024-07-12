package be.twofold.valen.ui;

import java.util.*;

public abstract class AbstractPresenter<T extends View> {
    private final T view;

    protected AbstractPresenter(T view) {
        this.view = Objects.requireNonNull(view);
    }

    public T getView() {
        return view;
    }
}
