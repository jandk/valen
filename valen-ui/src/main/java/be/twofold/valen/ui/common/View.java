package be.twofold.valen.ui.common;

import javafx.scene.*;

public interface View<T extends View.Listener> {

    Parent getFXNode();

    void setListener(T listener);

    /**
     * Marker interface for listener classes.
     */
    interface Listener {
    }

}
