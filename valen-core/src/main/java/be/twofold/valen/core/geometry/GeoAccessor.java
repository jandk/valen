package be.twofold.valen.core.geometry;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;

public record GeoAccessor<T extends Buffer>(
    int offset,
    int count,
    int stride,
    VertexBufferInfo<T> info,
    GeoReader<T> reader
) {
    @Deprecated
    public VertexBuffer<T> read(DataSource source) throws IOException {
        return Geo.readBuffer(source, this);
    }
}
