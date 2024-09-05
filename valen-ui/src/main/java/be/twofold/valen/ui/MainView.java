package be.twofold.valen.ui;

import be.twofold.valen.core.game.*;
import javafx.scene.control.*;

import java.util.*;

public interface MainView extends View {

    void setFileTree(TreeItem<String> root);

    void setAssets(List<Asset> resources);

    void setupPreview(Asset asset, Object assetData);

    void addListener(MainViewListener listener);

}
