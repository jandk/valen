package be.twofold.valen.core.export;

import java.io.*;
import java.util.*;

public interface Exporter<T> {

    String getExtension();

    Class<T> getSupportedType();

    void export(T value, OutputStream out) throws IOException;

    @SuppressWarnings("unchecked")
    static <T> List<Exporter<T>> forType(Class<T> type) {
        return ServiceLoader.load(Exporter.class).stream()
            .map(ServiceLoader.Provider::get)
            .filter(e -> e.getSupportedType().isAssignableFrom(type))
            .map(e -> (Exporter<T>) e)
            .toList();
    }

}
