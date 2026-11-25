package be.twofold.valen.core.geometry.read;

import be.twofold.valen.core.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

/**
 * How to read a VertexBuffer from a {@link BinaryReader}.
 * <p>
 * This is the read-time mirror of {@link VertexBuffer}.
 */
public record Accessor<T extends Slice>(
    int offset,
    int stride,
    AttributeReader<T> reader,
    AttributeLayout<T> layout
) {
    public Accessor {
        Check.nonNegative(offset, "offset");
        Check.nonNegative(stride, "stride");
        Check.nonNull(reader, "reader");
        Check.nonNull(layout, "layout");
    }

    public int count() {
        return layout.count();
    }

    public T allocate(int capacity) {
        return layout.allocate(capacity);
    }
}
