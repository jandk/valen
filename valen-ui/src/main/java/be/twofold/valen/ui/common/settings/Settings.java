package be.twofold.valen.ui.common.settings;

import be.twofold.valen.core.game.*;

import java.nio.file.*;
import java.util.*;

public final class Settings {
    private final Setting<Set<AssetType>> assetTypes = new Setting<>();
    private final Setting<Path> gameExecutable = new Setting<>();
    private final Setting<String> textureExporter = new Setting<>();
    private final Setting<Boolean> reconstructZ = new Setting<>();
    private final Setting<Path> exportPath = new Setting<>();

    Settings() {
    }

    public Setting<Set<AssetType>> assetTypes() {
        return assetTypes;
    }

    public Setting<Path> gameExecutable() {
        return gameExecutable;
    }

    public Setting<String> textureExporter() {
        return textureExporter;
    }

    public Setting<Boolean> reconstructZ() {
        return reconstructZ;
    }

    public Setting<Path> exportPath() {
        return exportPath;
    }
}
