package be.twofold.valen.ui.component;

import be.twofold.valen.core.game.*;
import javafx.scene.*;

public interface Viewer {

    Node getFXNode();

    String getName();

    boolean canPreview(AssetType type);

    void setData(Object data);

}
