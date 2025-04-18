package be.twofold.valen.core.geometry;

import java.nio.*;

public record GeoAccessor<T extends Buffer>(
    int offset,
    int count,
    int stride,
    VertexBufferInfo<T> info,
    GeoReader<T> reader
) {
}
