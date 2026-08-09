package be.twofold.valen.ui.common.settings;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.util.*;

import java.nio.file.*;
import java.util.*;

public final class Settings {
    private Set<AssetType> assetTypes = AssetType.ALL_NO_RAW;
    private Path gameExecutable = null;
    private String textureExporter = "texture.png";
    private String modelExporter = "gltf";
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
        textureExporter = Objects.requireNonNullElse(textureExporter, defaults.textureExporter);
        modelExporter = Objects.requireNonNullElse(modelExporter, defaults.modelExporter);
        reconstructZ = Objects.requireNonNullElse(reconstructZ, defaults.reconstructZ);
        treatAsRaw = Objects.requireNonNullElse(treatAsRaw, defaults.treatAsRaw);
        exportPath = Objects.requireNonNullElse(exportPath, defaults.exportPath);
    }

    public Optional<Path> getGameExecutable() {
        return Optional.ofNullable(gameExecutable);
    }

    public void setGameExecutable(Path gameExecutable) {
        this.gameExecutable = gameExecutable;
    }

    public String getTextureExporter() {
        return textureExporter;
    }

    public void setTextureExporter(String textureExporter) {
        this.textureExporter = Check.nonNull(textureExporter, "textureExporter");
    }

    public String getModelExporter() {
        return modelExporter;
    }

    public void setModelExporter(String modelExporter) {
        this.modelExporter = Check.nonNull(modelExporter, "modelExporter");
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
}
