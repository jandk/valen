package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public final class AssetLoader implements LoadingContext, Closeable {
    private final Archive archive;
    private final StorageManager storage;
    private final AssetReaders readers;

    public AssetLoader(
        Archive archive,
        StorageManager storage,
        AssetReaders readers
    ) {
        this.archive = Check.nonNull(archive, "archive");
        this.storage = Check.nonNull(storage, "storage");
        this.readers = Check.nonNull(readers, "readers");
    }

    public Archive archive() {
        return archive;
    }

    @Override
    public boolean exists(AssetID id) {
        return archive.get(id).isPresent();
    }

    @Override
    public <T> T load(AssetID id, Class<T> clazz) throws IOException {
        Asset asset = archive.get(id).orElseThrow();
        StorageLocation location = asset.location();

        try (BinarySource source = BinarySource.wrap(storage.open(location))) {
            AssetReader<T, Asset> reader = readers.find(asset, clazz);
            return reader.read(source, asset, this);
        }
    }

    @Override
    public Bytes open(StorageLocation location) throws IOException {
        return storage.open(location);
    }

    @Override
    public void close() throws IOException {
        storage.close();
    }
}
