package be.twofold.valen.ui.common;

import javafx.scene.*;

public interface View<T extends ViewListener> {

    Parent getFXNode();

    void setListener(T listener);

}
