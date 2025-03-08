package org.redeye.valen.game.spacemarines2.archives;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.util.zip.*;

public class ZipArchive implements Archive {
    private final Map<AssetID, Asset> assets = new HashMap<>();
    private final ZipFile zipFile;
    private final PackArchive parent;

    public ZipArchive(Path path, PackArchive parent) throws IOException {
        this.parent = parent;
        zipFile = new ZipFile(new File(path.toString()));
        var entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            var entry = entries.nextElement();
            if (!entry.isDirectory()) {
                var id = new EmperorAssetId(entry.getName());
                assets.put(id, new EmperorAsset(id, Math.toIntExact(entry.getSize())));
            }
        }
    }

    @Override
    public Stream<? extends Asset> assets() {
        return assets.values().stream();
    }

    @Override
    public Optional<? extends Asset> getAsset(AssetID identifier) {
        return Optional.ofNullable(assets.get(identifier));
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
        var asset = assets().filter(a -> a.id().equals(identifier)).findFirst().orElseThrow();
        try (var source = DataSource.fromArray(bytes)) {
            return clazz.cast(reader.get().read(parent, asset, source));
        }
    }
}
