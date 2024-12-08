package be.twofold.valen.ui.window;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.scene.control.*;

import java.nio.file.*;
import java.util.concurrent.*;

final class ExportTask extends Task<Void> {
    private final Path path;
    private final Archive archive;
    private final Asset asset;

    ExportTask(Path path, Archive archive, Asset asset) {
        this.path = Check.notNull(path, "path");
        this.archive = Check.notNull(archive, "archive");
        this.asset = Check.notNull(asset, "asset");
    }

    @Override
    protected Void call() {
        export().join();
        return null;
    }

    public CompletableFuture<Void> export() {
        return CompletableFuture.runAsync(this::export0);
    }

    void export0() {
        exporty(asset.type().clazz());
    }

    private <T> void exporty(Class<T> clazz) {
        var exporter = Exporter.forType(clazz).getFirst();
        try (var out = Files.newOutputStream(path)) {
            T rawAsset = archive.loadAsset(asset.id(), clazz);
            exporter.export(rawAsset, out);
        } catch (Exception e) {
            showExceptionDialog(e);
        }
    }

    private void showExceptionDialog(Exception e) {
        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Export failed");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        });
    }
}
