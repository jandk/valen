package be.twofold.valen.core.util;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public interface ValueEnum<K> {
    K value();

    static <K, E extends ValueEnum<K>> Map<K, E> valueMap(Class<E> enumClass) {
        return Arrays.stream(enumClass.getEnumConstants())
            .collect(Collectors.toUnmodifiableMap(ValueEnum::value, Function.identity()));
    }

    static <K, E extends ValueEnum<K>> Optional<E> fromValue(Map<K, E> valueMap, K value) {
        return Optional.ofNullable(valueMap.get(value));
    }
}
