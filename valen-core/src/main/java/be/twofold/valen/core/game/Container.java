package be.twofold.valen.core.game;

import wtf.reversed.toolbox.collect.*;

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

}
