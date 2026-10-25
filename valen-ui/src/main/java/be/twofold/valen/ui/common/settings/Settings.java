package be.twofold.valen.ui.common.settings;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.util.*;

import java.nio.file.*;
import java.util.*;

public final class Settings {
    private static final Map<AssetType, String> DEFAULTS = Map.of(
        AssetType.ANIMATION, "animation.gltf",
        AssetType.MATERIAL, "material.gltf",
        AssetType.MODEL, "model.gltf",
        AssetType.TEXTURE, "texture.png",
        AssetType.RAW, "binary.raw"
    );

    private Set<AssetType> assetTypes = AssetType.ALL_NO_RAW;
    private Map<AssetType, String> exporters = new EnumMap<>(AssetType.class);
    private Path gameExecutable = null;
    private String textureExporter;
    private String modelExporter;
    private Boolean reconstructZ = true;
    private Boolean treatAsRaw = false;
    private Path exportPath = Path.of("exported").toAbsolutePath();

    public Set<AssetType> getAssetTypes() {
        return copyOf(assetTypes);
    }

    public void setAssetTypes(Set<AssetType> assetTypes) {
        this.assetTypes = copyOf(Check.nonNull(assetTypes, "assetTypes"));
    }

    private static Set<AssetType> copyOf(Collection<AssetType> assetTypes) {
        // EnumSet.copyOf can't have an empty one, also fixes a stacktrace
        var result = EnumSet.noneOf(AssetType.class);
        assetTypes.stream()
            .filter(Objects::nonNull)
            .forEach(result::add);
        return result;
    }

    void normalize() {
        var defaults = new Settings();
        assetTypes = copyOf(Objects.requireNonNullElse(assetTypes, defaults.assetTypes));
        reconstructZ = Objects.requireNonNullElse(reconstructZ, defaults.reconstructZ);
        treatAsRaw = Objects.requireNonNullElse(treatAsRaw, defaults.treatAsRaw);
        exportPath = Objects.requireNonNullElse(exportPath, defaults.exportPath);
        migrate();
    }

    private void migrate() {
        if (textureExporter != null) {
            exporters.put(AssetType.TEXTURE, textureExporter);
            textureExporter = null;
        }
        if (modelExporter != null) {
            exporters.put(AssetType.ANIMATION, "animation." + modelExporter);
            exporters.put(AssetType.MATERIAL, "material." + modelExporter);
            exporters.put(AssetType.MODEL, "model." + modelExporter);
            modelExporter = null;
        }
    }

    public Optional<Path> getGameExecutable() {
        return Optional.ofNullable(gameExecutable);
    }

    public void setGameExecutable(Path gameExecutable) {
        this.gameExecutable = gameExecutable;
    }

    public Boolean isReconstructZ() {
        return reconstructZ;
    }

    public void setReconstructZ(Boolean reconstructZ) {
        this.reconstructZ = Check.nonNull(reconstructZ, "reconstructZ");
    }

    public Boolean isTreatAsRaw() {
        return treatAsRaw;
    }

    public void setTreatAsRaw(Boolean treatAsRaw) {
        this.treatAsRaw = Check.nonNull(treatAsRaw, "treatAsRaw");
    }

    public Path getExportPath() {
        return exportPath;
    }

    public void setExportPath(Path exportPath) {
        this.exportPath = Check.nonNull(exportPath, "exportPath");
    }

    public String getExporter(AssetType type) {
        Check.nonNull(type, "type");
        return exporters.getOrDefault(type, DEFAULTS.get(type));
    }

    public void setExporter(AssetType type, String exporter) {
        Check.nonNull(type, "type");
        Check.nonNull(exporter, "exporter");
        exporters.put(type, exporter);
    }
}
