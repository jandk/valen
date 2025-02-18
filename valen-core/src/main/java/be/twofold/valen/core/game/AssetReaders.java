package be.twofold.valen.core.game;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public final class AssetReaders<A extends Asset> {
    private final List<AssetReader<?, A>> readers;

    public AssetReaders(List<AssetReader<?, A>> readers) {
        this.readers = List.copyOf(readers);
    }

    public <R> R read(A asset, DataSource source, Class<R> clazz) throws IOException {
        var reader = readers.stream()
            .filter(ar -> ar.canRead(asset))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No reader found for asset: " + asset));

        return clazz.cast(reader.read(source, asset));
    }
}
