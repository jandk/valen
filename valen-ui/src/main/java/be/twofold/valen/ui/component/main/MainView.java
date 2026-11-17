package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import javafx.scene.*;

import java.nio.file.*;
import java.util.*;

public interface MainView extends View<MainView.Listener> {

    void setGames(List<GameEntry> games);

    void setCurrentGame(GameEntry game);

    void setArchives(List<String> archives);

    Object decodePreview(AssetType type, Object assetData, Meta.Node metadata);

    void displayPreview(Object preview);

    void clearPreview();

    void focusOnSearch();

    void setExporting(boolean exporting);

    void setPreviewLoading(boolean loading);

    void showSidePanel(SidePanel panel);

    void setFileListView(Node node);

    interface Listener extends View.Listener {

        void onGameSelected(Path executable);

        void onArchiveSelected(String name);

        void onSidePanelToggled(SidePanel panel);

        void onLoadGameClicked();

        void onExportClicked();

        void onSearchChanged(String query);

    }
}
