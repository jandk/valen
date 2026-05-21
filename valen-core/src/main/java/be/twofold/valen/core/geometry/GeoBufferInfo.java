package be.twofold.valen.core.geometry;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

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
        Check.nonNull(reader, "reader");
        Check.nonNull(elementType, "elementType");
        Check.nonNull(componentType, "componentType");
    }

    public int count() {
        return length * elementType().count();
    }

    public T allocate(int capacity) {
        return componentType.allocate(capacity);
    }
}
