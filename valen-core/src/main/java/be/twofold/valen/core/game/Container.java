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
        return new Container<K, V>() {
            @Override
            public Optional<V> get(K key) {
                return containers.stream()
                    .flatMap(file -> file.get(key).stream())
                    .findFirst();
            }

            @Override
            public Stream<V> getAll() {
                return containers.stream().flatMap(Container::getAll);
            }

            @Override
            public Bytes read(K key, Integer size) throws IOException {
                for (var file : containers) {
                    if (file.exists(key)) {
                        return file.read(key, size);
                    }
                }
                throw new FileNotFoundException("Unknown key: " + key);
            }

            @Override
            public void close() throws IOException {
                for (var file : containers) {
                    file.close();
                }
            }
        };
    }

}
