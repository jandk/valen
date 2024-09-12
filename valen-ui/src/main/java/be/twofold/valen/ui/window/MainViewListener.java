package be.twofold.valen.ui.window;

import be.twofold.valen.core.game.*;

public interface MainViewListener {

    void onPathSelected(String path);

    void onAssetSelected(Asset asset);

    void onPreviewVisibleChanged(boolean visible);

}
