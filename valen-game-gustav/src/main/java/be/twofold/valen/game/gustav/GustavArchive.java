package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.export.dds.*;
import be.twofold.valen.game.gustav.pak.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class GustavArchive implements Archive<GustavAssetID, GustavAsset> {
    private final AssetReaders<GustavAsset> readers;
    private final PakFile pakFile;

    public GustavArchive(Path path) throws IOException {
        this.pakFile = new PakFile(path);
        this.readers = new AssetReaders<>(List.of(
            DdsImporter.create()
        ));
    }

    @Override
    public <T> T loadAsset(GustavAssetID identifier, Class<T> clazz) throws IOException {
        var asset = get(identifier).orElseThrow(FileNotFoundException::new);

        var bytes = pakFile.read(identifier, null);
        try (var reader = BinaryReader.fromBytes(bytes)) {
            return readers.read(asset, reader, clazz);
        }
    }

    @Override
    public Optional<GustavAsset> get(GustavAssetID key) {
        return pakFile.get(key);
    }

    @Override
    public Stream<GustavAsset> getAll() {
        return pakFile.getAll();
    }

    @Override
    public void close() throws IOException {
        pakFile.close();
    }
}
