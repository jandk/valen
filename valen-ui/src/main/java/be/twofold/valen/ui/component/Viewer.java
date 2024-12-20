package be.twofold.valen.ui.component;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;

public interface Viewer extends FXView {

    String getName();

    boolean canPreview(AssetType<?> type);

    void setData(Object data);

}
