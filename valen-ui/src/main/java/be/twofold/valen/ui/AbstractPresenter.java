package be.twofold.valen.ui;

import be.twofold.valen.core.util.*;
import javafx.scene.*;

public abstract class AbstractPresenter<T> {
    private final T view;

    protected AbstractPresenter(T view) {
        this.view = Check.notNull(view);
    }

    public T getView() {
        return view;
    }

    public final Parent getFXNode() {
        if (!(view instanceof FXView fxView)) {
            throw new UnsupportedOperationException();
        }
        return fxView.getFXNode();
    }
}
