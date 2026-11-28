package be.twofold.valen.core.export;

import java.util.*;
import java.util.stream.*;

public final class Exporters {
    private static volatile Map<String, Object> properties = Map.of();

    private Exporters() {
    }

    public static void setProperties(Map<String, ?> properties) {
        Exporters.properties = Map.copyOf(properties);
    }

    @SuppressWarnings("unchecked")
    public static <T> Stream<Exporter<T>> forType(Class<T> type) {
        return ServiceLoader.load(Exporter.class).stream()
            .map(ServiceLoader.Provider::get)
            .filter(e -> e.getSupportedType().isAssignableFrom(type))
            .map(e -> (Exporter<T>) e);
    }

    public static <T> Exporter<T> forTypeAndId(Class<T> type, String id) {
        return forType(type)
            .filter(e -> e.getID().equals(id))
            .findFirst()
            .map(e -> {
                properties.forEach(e::setProperty);
                return e;
            })
            .orElseThrow();
    }
}
