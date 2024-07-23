package be.twofold.valen;

import be.twofold.valen.export.*;

import java.util.*;

public final class Exporters {
    private Exporters() {
    }

    @SuppressWarnings("unchecked")
    public static <T> List<Exporter<T>> forType(Class<T> type) {
        return ServiceLoader.load(Exporter.class).stream()
            .map(ServiceLoader.Provider::get)
            .filter(e -> e.getSupportedType().isAssignableFrom(type))
            .map(e -> (Exporter<T>) e)
            .toList();
    }
}
