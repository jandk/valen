package be.twofold.valen.ui.component.main;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import jakarta.inject.*;
import javafx.concurrent.*;
import org.slf4j.*;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

final class ExportService extends Service<Void> {
    private final Settings settings;
    private Archive<AssetID, Asset> archive;
    private List<Asset> assets;

    @Inject
    ExportService(Settings settings) {
        this.settings = settings;
    }

    public void setArchive(Archive<AssetID, Asset> archive) {
        this.archive = archive;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    @Override
    protected Task<Void> createTask() {
        final Archive<AssetID, Asset> archive = Check.notNull(this.archive, "archive");
        final List<Asset> assets = Check.notNull(this.assets, "assets");

        return new ExportTask(settings, archive, assets);
    }

    private static final class ExportTask extends Task<Void> {
        private static final Logger log = LoggerFactory.getLogger(ExportTask.class);
        private final List<AssetID> failedAssets = new ArrayList<>();

        private final Settings settings;
        private final Archive<AssetID, Asset> archive;
        private final List<Asset> assets;

        public ExportTask(Settings settings, Archive<AssetID, Asset> archive, List<Asset> assets) {
            this.settings = Check.notNull(settings, "settings");
            this.archive = Check.notNull(archive, "archive");
            this.assets = List.copyOf(assets);
        }

        @Override
        protected Void call() {
            for (int i = 0; i < assets.size(); i++) {
                updateProgress(i, assets.size());
                exportAsset(assets.get(i));
                if (isCancelled()) {
                    break;
                }
            }
            updateProgress(assets.size(), assets.size());
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
                exporter.setProperty("reconstructZ", settings.reconstructZ().get().orElse(false));
                exporter.setProperty("gltf.mode", settings.modelExporter().get().orElse("gltf"));

                var targetPath = findTargetPath(exporter, asset);
                if (Files.exists(targetPath)) {
                    // log.warn("Target already exists at {}", targetPath);
                    return;
                }

                @SuppressWarnings("unchecked")
                T rawAsset = (T) archive.loadAsset(asset.id(), asset.type().getType());
                Files.createDirectories(targetPath.getParent());
                exporter.export(rawAsset, targetPath);
            } catch (Exception e) {
                failedAssets.add(asset.id());
            }
        }

        @SuppressWarnings("unchecked")
        private <T> Exporter<T> findExporter(Asset asset) {
            var exporterId = asset.type() == AssetType.TEXTURE
                ? settings.textureExporter().get().orElse("texture.png")
                : null;
            var exporter = exporterId != null
                ? Exporter.forTypeAndId(asset.type().getType(), exporterId)
                : Exporter.forType(asset.type().getType()).findFirst().orElseThrow();
            return (Exporter<T>) exporter;
        }

        private Path findTargetPath(Exporter<?> exporter, Asset asset) {
            var basePath = settings.exportPath().get().orElse(Path.of("exported"));
            var filename = exporter.getExtension().isEmpty()
                ? asset.id().fileName()
                : asset.exportName() + "." + exporter.getExtension();
            return basePath
                .resolve(asset.id().pathName())
                .resolve(filename);
        }
    }
}
