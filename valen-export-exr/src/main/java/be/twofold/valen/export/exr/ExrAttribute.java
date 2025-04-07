package be.twofold.valen.export.exr;

import be.twofold.valen.core.util.*;

record ExrAttribute(
    String name,
    String type,
    Object value
) {
    public ExrAttribute {
        Check.notNull(name, "name");
        Check.notNull(type, "type");
        Check.notNull(value, "value");
    }

    public static <T> ExrAttribute create(ExrAttributeType<T> type, T value) {
        return new ExrAttribute(type.name(), type.name(), value);
    }
}
