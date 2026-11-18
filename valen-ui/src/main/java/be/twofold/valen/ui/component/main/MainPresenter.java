package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.component.preview.*;
import be.twofold.valen.ui.component.settings.*;
import be.twofold.valen.ui.service.*;

import java.nio.file.*;
import java.util.*;

public final class MainPresenter extends AbstractPresenter<MainView> implements MainView.Listener, FileListPresenter.Listener {
    private final FileListPresenter fileListPresenter;
    private final PreviewPresenter previewPresenter;

    private final GameService gameService;

    public MainPresenter(
        MainView view,
        FileListPresenter fileListPresenter,
        PreviewPresenter previewPresenter,
        SettingsPresenter settingsPresenter,
        GameService gameService,
        ExportService exportService,
        Session session,
        UiThread uiThread
    ) {
        super(view);

        this.fileListPresenter = fileListPresenter;
        this.previewPresenter = previewPresenter;
        this.gameService = gameService;

        fileListPresenter.setListener(this);
        settingsPresenter.setOnApplied(this::applySettings);
        view.setFileListView(fileListPresenter.getView());
        view.setPreviewView(previewPresenter.getView());
        view.setSettingsView(settingsPresenter.getView());
        view.setListener(this);

        session.gameProperty().subscribe(game -> uiThread.execute(() -> gameChanged(game)));
        session.installedGamesProperty().subscribe(games -> uiThread.execute(() -> getView().setGames(games)));
        exportService.progressProperty().subscribe(progress -> uiThread.execute(() -> getView().setExporting(progress != null)));
    }

    @Override
    public void onGameSelected(Path executable) {
        gameService.load(executable);
    }

    @Override
    public void onArchiveSelected(String name) {
        gameService.openArchive(name);
    }

    @Override
    public void onSidePanelToggled(SidePanel panel) {
        getView().showSidePanel(panel);
        previewPresenter.setVisible(panel == SidePanel.PREVIEW);
    }

    @Override
    public void onLoadGameClicked() {
        gameService.browse();
    }

    @Override
    public void onExportClicked() {
        fileListPresenter.exportSelected();
    }

    @Override
    public void onSearchChanged(String query) {
        fileListPresenter.setQuery(query);
    }

    @Override
    public void onAssetSelected(Asset asset, boolean forced) {
        // Set the asset first, so opening the panel loads it once instead of the previous one
        previewPresenter.setAsset(asset);
        if (forced) {
            getView().showSidePanel(SidePanel.PREVIEW);
            previewPresenter.setVisible(true);
        }
    }

    public void focusOnSearch() {
        getView().focusOnSearch();
    }

    private void gameChanged(Session.LoadedGame game) {
        getView().setCurrentGame(game != null ? game.entry() : null);
        getView().setArchives(game != null
            ? game.game().archiveNames().stream().sorted().toList()
            : List.of());
    }

    private void applySettings() {
        fileListPresenter.refresh();

        // "Treat as raw" changes how the current asset decodes, so reload it.
        previewPresenter.reload();
    }
}
