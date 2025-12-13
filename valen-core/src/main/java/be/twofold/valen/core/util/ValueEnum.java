package be.twofold.valen.core.util;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public interface ValueEnum<V> {
    static <V, E extends ValueEnum<V>> E fromValue(Class<E> enumClass, V value) {
        E enumValue = Lookup.lookup(enumClass, value);
        if (enumValue == null) {
            throw new NoSuchElementException("Unknown " + enumClass.getName() + " value: '" + value + "'");
        }
        return enumValue;
    }

    static <V, E extends ValueEnum<V>> Optional<E> fromValueOptional(Class<E> enumClass, V value) {
        return Optional.ofNullable(Lookup.lookup(enumClass, value));
    }

    V value();

    final class Lookup {
        private static final Map<Class<?>, Map<?, ?>> LOOKUP = new HashMap<>();

        private Lookup() {
        }

        @SuppressWarnings("unchecked")
        private static <K, E extends ValueEnum<K>> E lookup(Class<E> enumType, K value) {
            return (E) LOOKUP
                .computeIfAbsent(enumType, _ -> Arrays.stream(enumType.getEnumConstants())
                    .collect(Collectors.toUnmodifiableMap(ValueEnum::value, Function.identity())))
                .get(value);
        }
    }
}
