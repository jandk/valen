package be.twofold.valen.core.game;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;

public interface Container<K, V> extends Closeable {

    Optional<V> get(K key);

    Stream<V> getAll();

    ByteBuffer read(K key, int uncompressedSize) throws IOException;

    default ByteBuffer read(K key) throws IOException {
        return read(key, 0);
    }

    static <K, V> Container<K, V> compose(List<Container<K, V>> containers) {
        return new ContainerComposite<>(containers);
    }

}
