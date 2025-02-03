package be.twofold.valen.core.game;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public interface Container<K, V> extends Closeable {

    Optional<V> get(K key);

    Stream<V> getAll();

    byte[] read(K key, int uncompressedSize) throws IOException;

    default byte[] read(K key) throws IOException {
        return read(key, 0);
    }

    static <K, V> Container<K, V> compose(List<Container<K, V>> containers) {
        return new Container<>() {
            private final List<Container<K, V>> files = List.copyOf(containers);

            @Override
            public Optional<V> get(K key) {
                return files.stream()
                    .flatMap(file -> file.get(key).stream())
                    .findFirst();
            }

            @Override
            public Stream<V> getAll() {
                return files.stream().flatMap(Container::getAll);
            }

            @Override
            public byte[] read(K key, int uncompressedSize) throws IOException {
                for (var file : files) {
                    if (file.get(key).isPresent()) {
                        return file.read(key, uncompressedSize);
                    }
                }
                throw new IOException("Unknown key: " + key);
            }

            @Override
            public void close() throws IOException {
                for (var file : files) {
                    file.close();
                }
            }
        };
    }

}
