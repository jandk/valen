package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;

public record VertexBuffer<T extends Buffer>(
    T buffer,
    VertexBufferInfo<T> info
) {
    public VertexBuffer {
        Check.argument(
            buffer.limit() % info.size() == 0,
            () -> "buffer length must be a multiple of " + info.size()
        );
    }

    public int count() {
        return buffer.limit() / info.size();
    }
}
