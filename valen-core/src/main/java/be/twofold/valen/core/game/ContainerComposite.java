package be.twofold.valen.core.game;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;

final class ContainerComposite<K, V> implements Container<K, V> {
    private final List<Container<K, V>> files;

    public ContainerComposite(List<Container<K, V>> containers) {
        files = List.copyOf(containers);
    }

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
    public ByteBuffer read(K key, int uncompressedSize) throws IOException {
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
}
