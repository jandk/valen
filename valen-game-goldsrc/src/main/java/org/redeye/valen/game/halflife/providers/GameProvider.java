package org.redeye.valen.game.halflife.providers;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GameProvider implements Provider {
    private final Path root;
    private final FolderProvider looseFiles;
    private final Map<String, WadProvider> wads = new HashMap<>();

    public GameProvider(Path root) {
        this.root = root;
        try (var files = Files.walk(root, 1)) {
            files.forEach(file -> {
                if (file.getFileName().toString().endsWith(".wad")) {
                    try {
                        wads.put(file.getFileName().toString(), new WadProvider(file, this));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        looseFiles = new FolderProvider(root, this);
    }

    @Override
    public String getName() {
        return this.root.getFileName().toString();
    }

    @Override
    public Provider getParent() {
        return this;
    }

    @Override
    public List<Asset> assets() {
        return looseFiles.assets();
    }

    @Override
    public boolean exists(AssetID identifier) {
        return false;
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) {
        return null;
    }

    public List<WadProvider> getWads() {
        return wads.values().stream().toList();
    }
}
