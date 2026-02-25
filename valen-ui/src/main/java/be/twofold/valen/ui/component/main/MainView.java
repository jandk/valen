package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import javafx.scene.*;

import java.util.*;

public interface MainView extends View<MainView.Listener> {

    boolean isSidePaneVisible();

    void setArchives(List<String> archives);

    void setupPreview(Asset asset, Object assetData);

    void focusOnSearch();

    void setExporting(boolean exporting);

    void showPreview(boolean enabled);

    void showSettings(boolean enabled);

    void setFileListView(Node node);

    interface Listener extends View.Listener {

        void onArchiveSelected(String name);

        void onPreviewVisibilityChanged(boolean visible);

        void onSettingsVisibilityChanged(boolean visible);

        void onLoadGameClicked();

        void onExportClicked();

        void onSearchChanged(String query);

    }
}
