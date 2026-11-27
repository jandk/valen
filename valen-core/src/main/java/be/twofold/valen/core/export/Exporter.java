package be.twofold.valen.core.export;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;

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
}
