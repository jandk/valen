package be.twofold.valen.ui.component;

import be.twofold.valen.core.game.*;
import javafx.scene.*;

public interface Viewer {

    Node getFXNode();

    String getName();

    boolean canPreview(AssetType type);

    /**
     * Transforms data for display in the UI. Does NOT run on the FX thread.
     */
    default Object decode(Object data) {
        return data;
    }

    /**
     * Displays decoded object in the UI. Runs on the FX thread.
     */
    void display(Object payload);

}
