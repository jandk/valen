package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;

import java.util.*;

public interface MainView {

    boolean isPreviewVisible();

    void setArchives(List<String> archives);

    void setupPreview(Asset asset, Object assetData);

    void focusOnSearch();

    void setExporting(boolean exporting);

}
