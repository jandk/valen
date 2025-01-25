package be.twofold.valen.ui.common;

import javafx.scene.*;

public abstract class AbstractFXPresenter<T> extends AbstractPresenter<T> {
    protected AbstractFXPresenter(T view) {
        super(view);
    }

    public final Parent getFXNode() {
        if (!(getView() instanceof FXView fxView)) {
            throw new UnsupportedOperationException();
        }
        return fxView.getFXNode();
    }
}
