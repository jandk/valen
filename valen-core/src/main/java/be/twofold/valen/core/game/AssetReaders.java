package be.twofold.valen.core.game;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public final class AssetReaders<A extends Asset> {
    private final List<AssetReader<?, A>> readers;

    public AssetReaders(List<AssetReader<?, A>> readers) {
        var readersCopy = new ArrayList<>(readers);
        readersCopy.add(AssetReader.raw());
        this.readers = List.copyOf(readersCopy);
    }

    public <R> R read(A asset, BinaryReader reader, Class<R> clazz) throws IOException {
        var assetReader = readers.stream()
            .filter(ar -> ar.canRead(asset) && clazz.isAssignableFrom(ar.getReturnType()))
            .findFirst().orElseThrow(() -> new IOException("No reader found with type " + clazz + " for " + asset));

        return clazz.cast(assetReader.read(reader, asset));
    }
}
