package be.twofold.valen.ui;

import be.twofold.valen.core.game.*;
import javafx.scene.control.*;

import java.util.*;

public interface MainView extends View {

    boolean isPreviewVisible();

    void setFileTree(TreeItem<String> root);

    void setAssets(List<Asset> resources);

    void setupPreview(Asset asset, Archive archive);

    void addListener(MainViewListener listener);

}
