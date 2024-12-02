package be.twofold.valen.ui.window;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.util.*;

import java.util.*;

public interface MainView {

    boolean isPreviewVisible();

    void setArchives(List<String> archives);

    void setFileTree(PathNode<String> tree);

    void setFilteredAssets(List<Asset> assets);

    void setupPreview(Asset asset, Object assetData);

    void focusOnSearch();

    void setExporting(boolean exporting);

}
