package be.twofold.valen.ui.viewer;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.*;

public interface Viewer extends FXView {

    String getName();

    boolean canPreview(AssetType<?> type);

    void setData(Object data);

}
