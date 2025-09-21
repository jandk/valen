package be.twofold.valen.core.util;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public interface ValueEnum<V> {
    Map<Class<?>, Map<?, ?>> LOOKUP = new HashMap<>();

    static <V, E extends ValueEnum<V>> E fromValue(Class<E> enumClass, V value) {
        E enumValue = lookup(enumClass, value);
        if (enumValue == null) {
            throw new NoSuchElementException("Unknown " + enumClass.getName() + " value: '" + value + "'");
        }
        return enumValue;
    }

    static <V, E extends ValueEnum<V>> Optional<E> fromValueOptional(Class<E> enumClass, V value) {
        return Optional.ofNullable(lookup(enumClass, value));
    }

    @SuppressWarnings("unchecked")
    private static <K, E extends ValueEnum<K>> E lookup(Class<E> enumType, K value) {
        return (E) LOOKUP
            .computeIfAbsent(enumType, _ -> valueMap(enumType))
            .get(value);
    }

    private static <K, E extends ValueEnum<K>> Map<K, E> valueMap(Class<E> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
            .collect(Collectors.toUnmodifiableMap(ValueEnum::value, Function.identity()));
    }

    V value();
}
