package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;

import java.util.*;

public interface MainView extends View<MainViewListener> {

    boolean isSidePaneVisible();

    void setArchives(List<String> archives);

    void setupPreview(Asset asset, Object assetData);

    void focusOnSearch();

    void setExporting(boolean exporting);

    void showPreview(boolean enabled);

    void showSettings(boolean enabled);

    void setFileListView(javafx.scene.Node node);

}
