package be.twofold.valen.core.game;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public abstract class Archive<TID extends AssetID, TAsset extends Asset> implements Container<TID, TAsset> {
    private List<AssetReader<?, TAsset>> readers;

    public abstract List<AssetReader<?, TAsset>> createReaders();

    public final <T> T loadAsset(TID identifier, Class<T> clazz) throws IOException {
        var asset = get(identifier).orElseThrow(FileNotFoundException::new);
        var bytes = read(identifier, null);

        try (var source = BinaryReader.fromBytes(bytes)) {
            return read(asset, source, clazz);
        }
    }

    public <R> R read(TAsset asset, BinaryReader reader, Class<R> clazz) throws IOException {
        if (readers == null) {
            var readersCopy = new ArrayList<>(createReaders());
            readersCopy.add(AssetReader.raw());
            readers = List.copyOf(readersCopy);
        }
        var assetReader = readers.stream()
            .filter(ar -> ar.canRead(asset) && clazz.isAssignableFrom(ar.getReturnType()))
            .findFirst().orElseThrow(() -> new IOException("No reader found with type " + clazz + " for " + asset));

        return clazz.cast(assetReader.read(reader, asset));
    }
}
