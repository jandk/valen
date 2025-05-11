package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;

public record Hair(
    String name,
    IntBuffer segments,
    FloatBuffer positions
) {
    public Hair {
        Check.notNull(name, "name");
        Check.notNull(segments, "segments");
        Check.notNull(positions, "positions");
    }
}
