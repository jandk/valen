package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.scene.control.*;
import org.slf4j.*;

import java.nio.file.*;
import java.util.concurrent.*;

final class ExportTask extends Task<Void> {
    private static final Logger log = LoggerFactory.getLogger(ExportTask.class);

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
        exporty(asset.type().getType());
    }

    private <T> void exporty(Class<T> clazz) {
        var exporter = Exporter.forType(clazz).getFirst();
        try {
            T rawAsset = archive.loadAsset(asset.id(), clazz);
            exporter.export(rawAsset, path);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
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
