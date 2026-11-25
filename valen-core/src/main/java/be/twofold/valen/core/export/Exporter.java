package be.twofold.valen.core.export;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public interface Exporter<T> {
    String getID();

    String getName();

    String getExtension();

    Class<T> getSupportedType();

    default void setProperty(String key, Object value) {
    }

    void export(T value, BinaryWriter writer) throws IOException;

    default void export(T value, Path path) throws IOException {
        try (var writer = BinaryWriter.open(path)) {
            export(value, writer);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> Stream<Exporter<T>> forType(Class<T> type) {
        return ServiceLoader.load(Exporter.class).stream()
            .map(ServiceLoader.Provider::get)
            .filter(e -> e.getSupportedType().isAssignableFrom(type))
            .map(e -> (Exporter<T>) e);
    }

    static <T> Exporter<T> forTypeAndId(Class<T> type, String id) {
        return forType(type)
            .filter(e -> e.getID().equals(id))
            .findFirst().orElseThrow();
    }
}
