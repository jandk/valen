package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.component.preview.*;
import be.twofold.valen.ui.component.settings.*;

import java.nio.file.*;
import java.util.*;

public interface MainView extends View<MainView.Listener> {

    void setGames(List<GameEntry> games);

    void setCurrentGame(GameEntry game);

    void setArchives(List<String> archives);

    void focusOnSearch();

    void setExporting(boolean exporting);

    void showSidePanel(SidePanel panel);

    void setFileListView(FileListView fileListView);

    void setPreviewView(PreviewView previewView);

    void setSettingsView(SettingsView settingsView);

    interface Listener extends View.Listener {

        void onGameSelected(Path executable);

        void onArchiveSelected(String name);

        void onSidePanelToggled(SidePanel panel);

        void onLoadGameClicked();

        void onExportClicked();

        void onSearchChanged(String query);

    }
}
