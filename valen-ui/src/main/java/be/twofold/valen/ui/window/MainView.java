package be.twofold.valen.ui.window;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.*;
import javafx.scene.control.*;

import java.util.*;

public interface MainView extends View<MainViewListener> {

    boolean isPreviewVisible();

    void setArchives(List<String> archives);

    void setFileTree(TreeItem<String> root);

    void setFilteredAssets(List<Asset> assets);

    void setupPreview(Asset asset, Object assetData);

}
