package be.twofold.valen.format.cast;

import java.util.*;

record PropertyDef(
    String name,
    String key,
    Set<CastPropertyID> types,
    List<String> values,
    boolean isArray,
    boolean required
) {
    PropertyDef {
        Objects.requireNonNull(name);
        Objects.requireNonNull(key);
        if (types.isEmpty()) {
            throw new IllegalArgumentException("types is empty");
        }
        types = EnumSet.copyOf(types);
        values = List.copyOf(values);
    }


    public boolean isIndexed() {
        return key.endsWith("%d");
    }

    public boolean isSingular() {
        return types.size() == 1;
    }

    public boolean isBoolean() {
        return !values.isEmpty() && values.equals(List.of("True", "False"));
    }

    public boolean isEnum() {
        return !values.isEmpty() && !values.equals(List.of("True", "False"));
    }
}
