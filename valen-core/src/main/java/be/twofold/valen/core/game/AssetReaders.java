package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class AssetReaders {
    private final List<AssetReader<?, ?>> readers;

    public AssetReaders(List<AssetReader<?, ?>> readers) {
        readers = new ArrayList<>(readers);
        readers.add(new RawAssetReader());
        this.readers = List.copyOf(readers);
    }

    @SuppressWarnings("unchecked")
    public <T, A extends Asset> AssetReader<T, A> find(A asset, Class<T> clazz) throws IOException {
        return (AssetReader<T, A>) readers.stream()
            .filter(ar -> ((AssetReader<?, A>) ar).canRead(asset) && clazz.isAssignableFrom(ar.getReturnType()))
            .findFirst()
            .orElseThrow(() -> new IOException("No reader found with type " + clazz + " for " + asset));
    }

    private static final class RawAssetReader implements AssetReader<Bytes, Asset> {
        @Override
        public boolean canRead(Asset asset) {
            return true;
        }

        @Override
        public Bytes read(BinarySource source, Asset asset, LoadingContext context) throws IOException {
            return source.readBytes(Math.toIntExact(source.remaining()));
        }
    }
}
