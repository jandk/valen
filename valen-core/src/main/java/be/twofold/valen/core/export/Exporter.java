package be.twofold.valen.core.export;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public interface Exporter<T> {

    String getExtension();

    Class<T> getSupportedType();

    void export(T value, OutputStream out) throws IOException;

    default void export(T value, Path path) throws IOException {
        try (var out = Files.newOutputStream(path)) {
            export(value, out);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> List<Exporter<T>> forType(Class<T> type) {
        return ServiceLoader.load(Exporter.class).stream()
            .map(ServiceLoader.Provider::get)
            .filter(e -> e.getSupportedType().isAssignableFrom(type))
            .map(e -> (Exporter<T>) e)
            .toList();
    }

}
