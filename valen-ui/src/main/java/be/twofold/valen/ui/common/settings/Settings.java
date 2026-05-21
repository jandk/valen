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
    private Path exportPath = Path.of("exported").toAbsolutePath();

    public Set<AssetType> getAssetTypes() {
        return EnumSet.copyOf(assetTypes);
    }

    public void setAssetTypes(Set<AssetType> assetTypes) {
        this.assetTypes = EnumSet.copyOf(assetTypes);
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

    public Path getExportPath() {
        return exportPath;
    }

    public void setExportPath(Path exportPath) {
        this.exportPath = Check.nonNull(exportPath, "exportPath");
    }
}
