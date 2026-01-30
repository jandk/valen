package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.progress.*;
import jakarta.inject.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.scene.*;
import javafx.stage.*;
import org.slf4j.*;
import wtf.reversed.toolbox.util.*;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

final class ExportService extends Service<Void> {
    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    private final Settings settings;
    private final Stage stage;
    private final ProgressPresenter presenter;
    private Archive<AssetID, Asset> archive;
    private List<Asset> assets;

    @Inject
    ExportService(Settings settings, ViewLoader viewLoader) {
        this.settings = settings;
        this.presenter = viewLoader.loadPresenter(ProgressPresenter.class, "/fxml/Progress.fxml");
        this.stage = createStage(presenter.getView().getFXNode());

        presenter.setCancelHandler(this::cancel);

        progressProperty().addListener((_, _, _) -> update());
        messageProperty().addListener((_, _, _) -> update());
        workDoneProperty().addListener((_, _, _) -> update());
        totalWorkProperty().addListener((_, _, _) -> update());
    }

    private void update() {
        presenter.update(new Progress(getMessage(), getProgress(), getWorkDone(), getTotalWork()));
    }

    private Stage createStage(Parent parent) {
        var scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        var stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.setTitle("Exporting...");
        return stage;
    }

    public void setArchive(Archive<AssetID, Asset> archive) {
        this.archive = archive;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    @Override
    protected Task<Void> createTask() {
        Check.nonNull(this.archive, "archive");
        Check.nonNull(this.assets, "assets");

        ExportTask exportTask = new ExportTask();
        exportTask.setOnFailed(event -> Platform.runLater(() -> {
            FxUtils.showExceptionDialog(event.getSource().getException(), "Exception while exporting assets");
        }));
        return exportTask;
    }

    private final class ExportTask extends Task<Void> {
        private final List<AssetID> failedAssets = new ArrayList<>();

        @Override
        protected Void call() {
            Platform.runLater(() -> {
                if (!(stage.getWidth() > 0) || !(stage.getHeight() > 0)) {
                    // Don't have a size the first time yet, so force a show
                    stage.show();
                }

                Window window = Window.getWindows().getFirst();
                stage.setX(window.getX() + (window.getWidth() - stage.getWidth()) / 2);
                stage.setY(window.getY() + (window.getHeight() - stage.getHeight()) / 2);
                stage.show();
            });

            for (int i = 0; i < assets.size(); i++) {
                updateProgress(i, assets.size());
                exportAsset(assets.get(i));
                if (isCancelled()) {
                    break;
                }
            }
            updateProgress(assets.size(), assets.size());

            Platform.runLater(stage::hide);

            if (!failedAssets.isEmpty()) {
                String text = failedAssets.stream()
                    .map(AssetID::fullName)
                    .collect(Collectors.joining("\n"));
                FxUtils.showExceptionDialog(new Exception("Failed exporting some assets"), text);
            }
            return null;
        }

        private <T> void exportAsset(Asset asset) {
            updateMessage("Exporting " + asset.id().fullName());

            try {
                Exporter<T> exporter = findExporter(asset);
                exporter.setProperty("reconstructZ", settings.isReconstructZ());
                exporter.setProperty("gltf.mode", settings.getModelExporter());

                var targetPath = findTargetPath(exporter, asset);
                if (Files.exists(targetPath)) {
                    // log.warn("Target already exists at {}", targetPath);
                    return;
                }

                @SuppressWarnings("unchecked")
                T rawAsset = (T) archive.loadAsset(asset.id(), asset.type().type());
                Files.createDirectories(targetPath.getParent());
                exporter.export(rawAsset, targetPath);
            } catch (Exception e) {
                log.warn("Failed exporting asset", e);
                failedAssets.add(asset.id());
            }
        }

        @SuppressWarnings("unchecked")
        private <T> Exporter<T> findExporter(Asset asset) {
            boolean isGltf = Set.of("glb", "gltf").contains(settings.getModelExporter());
            var exporterId = switch (asset.type()) {
                case ANIMATION -> "animation." + (isGltf ? "gltf" : "cast");
                case MATERIAL -> "material." + (isGltf ? "gltf" : "cast");
                case MODEL -> "model." + (isGltf ? "gltf" : "cast");
                case TEXTURE -> settings.getTextureExporter();
                case RAW -> "binary.raw";
            };
            var exporter = exporterId != null
                ? Exporter.forTypeAndId(asset.type().type(), exporterId)
                : Exporter.forType(asset.type().type()).findFirst().orElseThrow();
            return (Exporter<T>) exporter;
        }

        private Path findTargetPath(Exporter<?> exporter, Asset asset) {
            var basePath = settings.getExportPath();
            var filename = exporter.getExtension().isEmpty()
                ? asset.id().fileName()
                : asset.exportName() + "." + exporter.getExtension();
            return basePath
                .resolve(asset.id().pathName())
                .resolve(filename);
        }
    }
}
