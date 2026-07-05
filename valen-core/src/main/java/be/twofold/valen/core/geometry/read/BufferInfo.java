package be.twofold.valen.core.geometry.read;

import be.twofold.valen.core.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

public record BufferInfo<T extends Slice>(
    int offset,
    int stride,
    AttributeReader<T> reader,
    AttributeLayout<T> layout
) {
    public BufferInfo {
        Check.positiveOrZero(offset, "offset");
        Check.positiveOrZero(stride, "stride");
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
