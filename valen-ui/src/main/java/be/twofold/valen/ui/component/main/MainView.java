package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import javafx.scene.*;

import java.util.*;

public interface MainView extends View<MainView.Listener> {

    void setArchives(List<String> archives);

    void setupPreview(Asset asset, Object assetData, Meta.Node metadata);

    void focusOnSearch();

    void setExporting(boolean exporting);

    void showSidePanel(SidePanel panel);

    void setFileListView(Node node);

    interface Listener extends View.Listener {

        void onArchiveSelected(String name);

        void onSidePanelToggled(SidePanel panel);

        void onLoadGameClicked();

        void onExportClicked();

        void onSearchChanged(String query);

    }
}
