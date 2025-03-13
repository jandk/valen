package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.event.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.events.*;
import jakarta.inject.*;
import javafx.application.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public final class MainPresenter extends AbstractFXPresenter<MainView> {
    private final SendChannel<MainEvent> channel;
    private final FileListPresenter fileList;
    private final Settings settings;

    private @Nullable Game game;
    private @Nullable Archive archive;
    private @Nullable Asset lastAsset;
    private String query = "";

    @Inject
    MainPresenter(
        MainView view,
        EventBus eventBus,
        FileListPresenter fileList,
        // SettingsPresenter settingsPresenter,
        Settings settings
    ) {
        super(view);

        this.channel = eventBus.senderFor(MainEvent.class);
        this.fileList = fileList;
        this.settings = settings;

        eventBus
            .receiverFor(MainViewEvent.class)
            .consume(event -> {
                switch (event) {
                    case MainViewEvent.ArchiveSelected(var name) -> selectArchive(name);
                    case MainViewEvent.PreviewVisibilityChanged(var visible) -> showPreview(visible);
                    case MainViewEvent.SettingVisibilityChanged(var visible) -> showSettings(visible);
                    case MainViewEvent.LoadGameClicked _ -> channel.send(new MainEvent.GameLoadRequested());
                    case MainViewEvent.ExportClicked() -> exportSelectedAsset();
                    case MainViewEvent.SearchChanged(var query) -> {
                        this.query = query;
                        updateFileList();
                    }
                }
            });

        eventBus
            .receiverFor(AssetSelected.class)
            .consume(event -> selectAsset(event.asset()));

        eventBus
            .receiverFor(SettingsApplied.class)
            .consume(_ -> updateFileList());
    }

    private void selectArchive(String archiveName) {
        if (game == null) {
            return;
        }
        try {
            archive = game.loadArchive(archiveName);
            updateFileList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void selectAsset(Asset asset) {
        if (getView().isSidePaneVisible() && archive != null) {
            try {
                var type = switch (asset.type()) {
                    case MODEL, TEXTURE -> asset.type().getType();
                    default -> ByteBuffer.class;
                };
                var assetData = archive.loadAsset(asset.id(), type);
                Platform.runLater(() -> getView().setupPreview(asset, assetData));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        lastAsset = asset;
    }

    private void showPreview(boolean visible) {
        if (visible && lastAsset != null) {
            selectAsset(lastAsset);
        }
    }

    private void showSettings(boolean visible) {
    }

    private void exportSelectedAsset() {
        var asset = fileList.getSelectedAsset();
        if (asset == null) {
            return;
        }

        // TODO: Clean this up
        var exporterId = asset.type() == AssetType.TEXTURE
            ? settings.textureExporter().get().orElse("texture.png")
            : null;
        var exporter = exporterId != null
            ? Exporter.forTypeAndId(asset.type().getType(), exporterId)
            : Exporter.forType(asset.type().getType()).findFirst().orElseThrow();
        var filename = exporter.getExtension().isEmpty()
            ? asset.id().fileName()
            : Filenames.removeExtension(asset.id().fileName()) + "." + exporter.getExtension();

        channel.send(new MainEvent.SaveFileRequested(filename, path -> {
            getView().setExporting(true);

            var exportTask = new ExportTask<>(exporter, archive, asset, path);
            CompletableFuture
                .runAsync(exportTask::export)
                .handle((_, _) -> {
                    getView().setExporting(false);
                    return null;
                });
        }));
    }

    private void updateFileList() {
        if (archive == null) {
            return;
        }
        var predicate = buildPredicate(query, settings.assetTypes().get().orElse(Set.of()));
        var assets = archive.getAll().filter(predicate);
        fileList.setAssets(assets);
    }

    private Predicate<Asset> buildPredicate(String assetName, Collection<AssetType> assetTypes) {
        List<Predicate<Asset>> predicates = new ArrayList<>();
        if (assetName != null) {
            predicates.add(asset -> asset.id().fullName().contains(assetName));
        }
        if (assetTypes != null && !assetTypes.isEmpty()) {
            var assetTypeSet = EnumSet.copyOf(assetTypes);
            predicates.add(asset -> assetTypeSet.contains(asset.type()));
        }
        return predicates.stream()
            .reduce(Predicate::and)
            .orElse(_ -> true);
    }

    public void setGame(Game game) {
        this.game = game;
        getView().setArchives(game.archiveNames().stream().sorted().toList());
    }

    public void focusOnSearch() {
        getView().focusOnSearch();
    }
}
