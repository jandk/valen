package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

public final class AssetLoader implements LoadingContext, Closeable {
    private final Archive archive;
    private final StorageManager storage;
    private final List<AssetReader<?, ?>> readers;

    public AssetLoader(
        Archive archive,
        StorageManager storage,
        List<AssetReader<?, ?>> readers
    ) {
        this.archive = Check.nonNull(archive, "archive");
        this.storage = Check.nonNull(storage, "storage");

        // Sneak in raw at the end
        readers = new ArrayList<>(readers);
        readers.add(new AssetReader.Raw());
        this.readers = List.copyOf(readers);
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
            AssetReader<T, Asset> reader = findReader(asset, clazz);
        return reader.read(asset, this);
    }

    @Override
    public Bytes open(Location location) throws IOException {
        return storage.open(location);
    }

    @Override
    public void close() throws IOException {
        storage.close();
    }

    @SuppressWarnings("unchecked")
    public <T, A extends Asset> AssetReader<T, A> findReader(A asset, Class<T> clazz) throws IOException {
        return (AssetReader<T, A>) readers.stream()
            .filter(ar -> ((AssetReader<?, A>) ar).canRead(asset) && clazz.isAssignableFrom(ar.getReturnType()))
            .findFirst()
            .orElseThrow(() -> new IOException("No reader found with type " + clazz + " for " + asset));
    }
}
