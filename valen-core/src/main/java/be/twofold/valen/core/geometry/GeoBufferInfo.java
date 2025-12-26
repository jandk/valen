package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

public record GeoBufferInfo<T extends Slice>(
    int offset,
    int stride,
    int length,
    GeoReader<T> reader,
    ElementType elementType,
    ComponentType<T> componentType
) {
    public GeoBufferInfo {
        Check.positiveOrZero(offset, "offset");
        Check.positiveOrZero(stride, "stride");
        Check.positive(length, "length");
        Check.notNull(reader, "reader");
        Check.notNull(elementType, "elementType");
        Check.notNull(componentType, "componentType");
    }

    public int count() {
        return length * elementType().count();
    }

    public T allocate(int capacity) {
        return componentType.allocate(capacity);
    }
}
