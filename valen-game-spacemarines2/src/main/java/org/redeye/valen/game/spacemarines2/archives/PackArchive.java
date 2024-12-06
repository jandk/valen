package org.redeye.valen.game.spacemarines2.archives;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PackArchive implements Archive {

    private final List<ZipArchive> mounted = new ArrayList<>();

    public PackArchive(Path root, String name) throws IOException {
        String packName = switch (name) {
            case "client_pc" -> "client";
            case "server_pc" -> "server";
            default -> throw new IllegalStateException("Unexpected Pack name: " + name);
        };
        var paksFolder = root.resolve("%s\\root\\paks\\%s".formatted(name, packName));
        mounted.add(new ZipArchive(paksFolder.resolve("resources.pak"), this));
        try (var files = Files.walk(paksFolder.resolve("default"))) {
            files.forEach(file -> {
                if (!file.toString().endsWith(".pak")) {
                    return;
                }
                try {
                    mounted.add(new ZipArchive(file, this));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        try (var files = Files.walk(paksFolder.resolve("default/scenes"))) {
            files.forEach(file -> {
                if (!file.toString().endsWith(".pak")) {
                    return;
                }
                try {
                    mounted.add(new ZipArchive(file, this));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public List<Asset> assets() {
        return mounted.stream().flatMap(zipArchive -> zipArchive.assets().stream()).toList();
    }

    @Override
    public boolean exists(AssetID identifier) {
        return mounted.stream().anyMatch(zipArchive -> zipArchive.exists(identifier));
    }


    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        for (ZipArchive zipArchive : mounted) {
            if (zipArchive.exists(identifier)) {
                return zipArchive.loadAsset(identifier, clazz);
            }
        }
        throw new FileNotFoundException("File '" + identifier + "' not found");
    }
}
