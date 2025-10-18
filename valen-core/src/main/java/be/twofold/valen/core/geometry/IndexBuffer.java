package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

public record IndexBuffer(
    Ints indices
) {
    public IndexBuffer {
        Check.notNull(indices, "indices");
        Check.argument(indices.size() % 3 == 0, "indices.size() % 3 != 0");
    }

    public int faceCount() {
        return indices.size() / 3;
    }

    public boolean isEmpty() {
        return indices.size() == 0;
    }
}
