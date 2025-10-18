package be.twofold.valen.core.game;

import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public interface Container<K, V> extends Closeable {

    Optional<V> get(K key);

    default boolean exists(K identifier) {
        return get(identifier).isPresent();
    }

    Stream<V> getAll();

    Bytes read(K key, Integer size) throws IOException;

    static <K, V> Container<K, V> compose(List<Container<K, V>> containers) {
        return new ContainerComposite<>(containers);
    }

}
