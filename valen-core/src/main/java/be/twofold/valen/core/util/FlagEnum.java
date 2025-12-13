package be.twofold.valen.core.util;

import java.util.*;

public interface FlagEnum {
    int value();

    static <E extends Enum<E> & FlagEnum> Set<E> fromValue(Class<E> enumClass, int value) {
        EnumSet<E> result = EnumSet.noneOf(enumClass);
        for (E flag : Lookup.lookup(enumClass)) {
            int flagValue = flag.value();
            if ((value & flagValue) == flagValue) {
                result.add(flag);
                value &= ~flagValue;
            }
        }
        if (value != 0) {
            throw new IllegalArgumentException("Unknown bits: 0x" + Integer.toHexString(value));
        }
        return result;
    }

    static <E extends Enum<E> & FlagEnum> int toValue(Set<E> flags) {
        return flags.stream()
            .mapToInt(FlagEnum::value)
            .reduce(0, (a, b) -> a | b);
    }

    final class Lookup {
        private static final Map<Class<?>, Enum<?>[]> LOOKUP = new HashMap<>();

        private Lookup() {
        }

        @SuppressWarnings("unchecked")
        private static <E extends Enum<E>> E[] lookup(Class<E> enumClass) {
            return (E[]) LOOKUP.computeIfAbsent(enumClass, aClass -> (Enum<?>[]) aClass.getEnumConstants());
        }
    }
}
