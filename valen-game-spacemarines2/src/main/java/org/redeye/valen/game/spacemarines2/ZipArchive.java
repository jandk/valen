package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class ZipArchive implements Archive {
    private final ZipFile zipFile;
    private final List<Asset> assets = new ArrayList<>();

    public ZipArchive(Path path) throws IOException {
        zipFile = new ZipFile(new File(path.toString()));
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.isDirectory()) {
                continue;
            }
            EmperorAssetId emperorAssetId = new EmperorAssetId(entry.getName());
            assets.add(new Asset(emperorAssetId, emperorAssetId.inferAssetType(), (int) entry.getSize(), Map.of()));
        }
    }

    @Override
    public List<Asset> assets() {
        return assets;
    }

    @Override
    public boolean exists(AssetID identifier) {
        return zipFile.getEntry(identifier.fullName()) != null;
    }

    @Override
    public Object loadAsset(AssetID identifier) throws IOException {
        return null;
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID identifier) throws IOException {
        var entry = zipFile.getEntry(identifier.fullName());
        if (entry == null) {
            throw new FileNotFoundException(identifier.fullName());
        }
        return ByteBuffer.wrap(zipFile.getInputStream(entry).readAllBytes());
    }
}
