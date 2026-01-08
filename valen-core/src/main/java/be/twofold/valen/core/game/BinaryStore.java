package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.util.*;

public interface BinaryStore<K> extends Closeable {

    boolean exists(K key);

    default Bytes read(K key) throws IOException {
        return read(key, OptionalInt.empty());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Bytes read(K key, OptionalInt size) throws IOException;

    static <K> BinaryStore<K> compose(List<BinaryStore<K>> stores) {
        return new BinaryStore<K>() {
            @Override
            public boolean exists(K key) {
                for (var store : stores) {
                    if (store.exists(key)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Bytes read(K key, OptionalInt size) throws IOException {
                for (var store : stores) {
                    if (store.exists(key)) {
                        return store.read(key, size);
                    }
                }
                throw new FileNotFoundException("Unknown key: " + key);
            }

            @Override
            public void close() throws IOException {
                for (var store : stores) {
                    store.close();
                }
            }
        };
    }

}
