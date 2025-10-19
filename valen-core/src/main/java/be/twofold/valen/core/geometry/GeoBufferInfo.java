package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

public record GeoBufferInfo<T extends WrappedArray>(
    int offset,
    int stride,
    int length,
    GeoReader<T> reader,
    ComponentType<T> componentType
) {
    public GeoBufferInfo {
        Check.positiveOrZero(offset, "offset");
        Check.positive(stride, "stride");
        Check.positive(length, "length");
        Check.notNull(reader, "reader");
        Check.notNull(componentType, "componentType");
    }

    public T allocate(int capacity) {
        return componentType.allocate(capacity);
    }
}
