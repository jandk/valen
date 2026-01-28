package be.twofold.valen.ui.common;

import javafx.scene.*;
import wtf.reversed.toolbox.util.*;

public abstract class AbstractPresenter<T> {
    private final T view;

    protected AbstractPresenter(T view) {
        this.view = Check.nonNull(view, "view");
    }

    public final T getView() {
        return view;
    }

    public final Parent getFXNode() {
        if (!(getView() instanceof View<?> view)) {
            throw new UnsupportedOperationException();
        }
        return view.getFXNode();
    }
}
