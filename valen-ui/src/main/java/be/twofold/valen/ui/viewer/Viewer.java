package be.twofold.valen.ui.viewer;

import be.twofold.valen.core.game.*;
import javafx.scene.*;

public interface Viewer {

    boolean canPreview(AssetType type);

    void setData(Object data);

    Node getFXNode();

    String getName();
}
