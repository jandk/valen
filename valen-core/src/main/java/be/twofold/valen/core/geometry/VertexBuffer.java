package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

public record VertexBuffer<T extends WrappedArray>(
    T buffer,
    VertexBufferInfo<T> info
) {
    public VertexBuffer {
        Check.argument(buffer.size() % info.size() == 0, "buffer.size() % info.size() != 0");
    }
}
