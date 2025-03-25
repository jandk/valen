package be.twofold.valen.core.game;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;

final class ContainerComposite<K, V> implements Container<K, V> {
    private final List<Container<K, V>> containers;

    public ContainerComposite(List<Container<K, V>> containers) {
        this.containers = List.copyOf(containers);
    }

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
    public ByteBuffer read(K key, Integer size) throws IOException {
        for (var file : containers) {
            if (file.exists(key)) {
                return file.read(key, size);
            }
        }
        throw new IOException("Unknown key: " + key);
    }

    @Override
    public void close() throws IOException {
        for (var file : containers) {
            file.close();
        }
    }
}
