package be.twofold.valen.ui;

import be.twofold.valen.core.game.*;

public interface MainViewListener {

    void onPathSelected(String path);

    void onAssetSelected(Asset<?> asset);

}
