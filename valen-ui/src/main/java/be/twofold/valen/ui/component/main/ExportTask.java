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

final class ExportTask<T> extends Task<Void> {
    private static final Logger log = LoggerFactory.getLogger(ExportTask.class);

    private final Exporter<T> exporter;
    private final Archive archive;
    private final Asset asset;
    private final Path path;

    ExportTask(Exporter<T> exporter, Archive archive, Asset asset, Path path) {
        this.exporter = Check.notNull(exporter, "exporter");
        this.archive = Check.notNull(archive, "archive");
        this.asset = Check.notNull(asset, "asset");
        this.path = Check.notNull(path, "path");
        Check.argument(exporter.getSupportedType() == asset.type().getType(), () ->
            String.format("Exporter type (%s) does not match asset type (%s)", exporter.getSupportedType(), asset.type()));
    }

    @Override
    protected Void call() {
        CompletableFuture.runAsync(this::export).join();
        return null;
    }

    @SuppressWarnings("unchecked")
    void export() {
        try {
            T rawAsset = (T) archive.loadAsset(asset.id(), asset.type().getType());
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
