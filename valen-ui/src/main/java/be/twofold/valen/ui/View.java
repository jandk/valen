package be.twofold.valen.ui;

import javafx.scene.*;

public interface View<TListener> {

    Parent getView();

    void addListener(TListener listener);

    void removeListener(TListener listener);

}
