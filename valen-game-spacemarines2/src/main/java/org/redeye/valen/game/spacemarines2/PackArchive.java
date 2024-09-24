package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.readers.Reader;
import org.redeye.valen.game.spacemarines2.readers.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public class PackArchive implements Archive {
    private static final List<Reader<?>> READERS = List.of(new Resource(), new TPLResource(), new PCTResource());

    private final Path root;
    private final String name;

    private final List<ZipArchive> mounted = new ArrayList<>();

    public PackArchive(Path root, String name) throws IOException {
        this.root = root;
        this.name = switch (name) {
            case "client_pc" -> "client";
            case "server_pc" -> "server";
            default -> throw new IllegalStateException("Unexpected Pack name: " + name);
        };
        var paksFolder = this.root.resolve("%s\\root\\paks\\%s".formatted(name, this.name));
        mounted.add(new ZipArchive(paksFolder.resolve("resources.pak")));
        Files.walk(paksFolder.resolve("default")).forEach(file -> {
            if (!file.toString().endsWith(".pak")) {
                return;
            }
            try {
                mounted.add(new ZipArchive(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

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
    public Object loadAsset(AssetID identifier) throws IOException {
        var reader = READERS.stream().filter(r -> r.canRead(identifier)).findFirst();
        if (reader.isEmpty()) {
            return null;
        }
        var raw = loadRawAsset(identifier);
        var asset = assets().stream().filter(a -> a.id().equals(identifier)).findFirst().orElseThrow();
        return reader.get().read(this, asset, DataSource.fromBuffer(raw));
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID identifier) throws IOException {
        for (ZipArchive zipArchive : mounted) {
            if (zipArchive.exists(identifier)) {
                return zipArchive.loadRawAsset(identifier);
            }
        }
        throw new FileNotFoundException("File '" + identifier + "' not found");
    }
}
