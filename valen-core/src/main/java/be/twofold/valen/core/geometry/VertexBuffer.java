package be.twofold.valen.core.geometry;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

public record VertexBuffer<T extends Slice>(
    T array,
    AttributeLayout<T> layout
) {
    public VertexBuffer {
        Check.nonNull(array, "array");
        Check.nonNull(layout, "layout");
    }

    public int length() {
        return layout.length();
    }

    public int count() {
        return layout.count();
    }
}
