package be.twofold.valen.ui.service;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import jakarta.inject.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

@Singleton
public final class ExportService {
    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        var thread = new Thread(runnable, "exporter");
        thread.setDaemon(true);
        return thread;
    });

    private final Value<ExportProgress> progress = new Value<>();
    private final Settings settings;
    private final Dialogs dialogs;
    private volatile boolean cancelled;

    @Inject
    ExportService(Settings settings, Dialogs dialogs) {
        this.settings = settings;
        this.dialogs = dialogs;
    }

    public ReadOnlyValue<ExportProgress> progressProperty() {
        return progress;
    }

    public void export(AssetLoader loader, List<? extends Asset> assets) {
        if (assets.isEmpty()) {
            return;
        }
        var sorted = assets.stream().sorted().toList();
        executor.execute(() -> run(loader, sorted));
    }

    public void cancel() {
        cancelled = true;
    }

    private void run(AssetLoader loader, List<? extends Asset> assets) {
        cancelled = false;
        var failures = new LinkedHashMap<String, List<ExportFailure>>();
        var lastPublish = 0L;
        try {
            for (var i = 0; i < assets.size() && !cancelled; i++) {
                var asset = assets.get(i);
                var now = System.nanoTime();
                if (i == 0 || now - lastPublish >= 50_000_000) {
                    progress.set(new ExportProgress(i, assets.size(), "Exporting " + asset.id().fullName()));
                    lastPublish = now;
                }
                exportAsset(loader, asset, failures);
            }
        } catch (Exception e) {
            log.error("Export failed", e);
            dialogs.showException("Export failed", e);
        } finally {
            progress.set(null);
        }

        if (!failures.isEmpty()) {
            dialogs.showExportFailures(assets.size(), failures);
        }
    }

    private <T> void exportAsset(AssetLoader loader, Asset asset, Map<String, List<ExportFailure>> failures) {
        Path targetPath = null;
        try {
            var type = settings.isTreatAsRaw() ? AssetType.RAW : asset.type();

            Exporter<T> exporter = findExporter(type);
            exporter.setProperty("reconstructZ", settings.isReconstructZ());

            targetPath = findTargetPath(exporter, asset);
            if (Files.exists(targetPath)) {
                return;
            }

            @SuppressWarnings("unchecked")
            T rawAsset = (T) loader.load(asset.id(), type.type());
            Files.createDirectories(targetPath.getParent());
            exporter.export(rawAsset, targetPath);
        } catch (Exception e) {
            var root = Throwables.getRootCause(e);
            var failed = failures.computeIfAbsent(failureKey(root), _ -> new ArrayList<>());
            if (failed.isEmpty()) {
                log.warn("Failed exporting {}", asset.id().fullName(), e);
            } else {
                log.debug("Failed exporting {}: {}", asset.id().fullName(), root.toString());
            }
            failed.add(new ExportFailure(asset.id(), Throwables.getMessage(root)));
            discardPartialExport(targetPath);
        }
    }

    private String failureKey(Throwable root) {
        for (StackTraceElement frame : root.getStackTrace()) {
            String className = frame.getClassName();
            if (className.startsWith("be.twofold.") || className.startsWith("wtf.reversed.")) {
                return root.getClass().getSimpleName() + " at "
                    + className.substring(className.lastIndexOf('.') + 1) + "." + frame.getMethodName()
                    + ":" + frame.getLineNumber();
            }
        }
        return root.getClass().getSimpleName();
    }

    // In case we fail, remove the file, so the next run writes it again
    private void discardPartialExport(Path targetPath) {
        if (targetPath == null) {
            return;
        }
        try {
            Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            log.warn("Could not delete partial export {}", targetPath, e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Exporter<T> findExporter(AssetType type) {
        var exporterId = settings.getExporter(type);
        var exporter = exporterId != null
            ? Exporter.forTypeAndId(type.type(), exporterId)
            : Exporter.forType(type.type()).findFirst().orElseThrow();
        return (Exporter<T>) exporter;
    }

    private Path findTargetPath(Exporter<?> exporter, Asset asset) {
        var path = asset.id().path();
        if (!exporter.getExtension().isEmpty()) {
            path = path.parent().resolve(asset.exportName() + "." + exporter.getExtension());
        }
        return path.toPath(settings.getExportPath());
    }
}
