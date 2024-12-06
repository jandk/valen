package org.redeye.valen.game.spacemarines2.archives;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public class ZipArchive implements Archive {
    private final ZipFile zipFile;
    private final List<Asset> assets = new ArrayList<>();
    private final PackArchive parent;

    public ZipArchive(Path path, PackArchive parent) throws IOException {
        this.parent = parent;
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
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        var entry = zipFile.getEntry(identifier.fullName());
        if (entry == null) {
            throw new FileNotFoundException(identifier.fullName());
        }
        var bytes = zipFile.getInputStream(entry).readAllBytes();

        if (clazz == byte[].class) {
            return (T) bytes;
        }

        var reader = AllReaders.READERS.stream().filter(r -> r.canRead(identifier)).findFirst();
        if (reader.isEmpty()) {
            return null;
        }
        var asset = assets().stream().filter(a -> a.id().equals(identifier)).findFirst().orElseThrow();
        try (var source = DataSource.fromArray(bytes)) {
            return clazz.cast(reader.get().read(parent, asset, source));
        }
    }
}
