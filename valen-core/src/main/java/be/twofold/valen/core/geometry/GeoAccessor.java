package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.collect.*;

public record GeoAccessor<T extends WrappedArray>(
    int offset,
    int stride,
    VertexBufferInfo<T> info,
    GeoReader<T> reader
) {
}
