package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public record VertexBuffer2(
    FloatBuffer positions,
    Optional<FloatBuffer> normals,
    Optional<FloatBuffer> tangents,
    List<FloatBuffer> texCoords,
    Optional<ShortBuffer> joints,
    Optional<FloatBuffer> weights,
    Optional<ByteBuffer> colors
) {
    public VertexBuffer2 {
        Check.notNull(positions, "positions");
        normals.ifPresent(fb -> check(fb, positions.limit(), 3));
        tangents.ifPresent(fb -> check(fb, positions.limit(), 4));
        for (var texCoord : texCoords) {
            check(texCoord, positions.limit(), 2);
        }
        joints.ifPresent(sb -> check(sb, positions.limit(), 4));
        weights.ifPresent(fb -> check(fb, positions.limit(), 4));
        colors.ifPresent(bb -> check(bb, positions.limit(), 4));
    }

    private void check(Buffer buffer, int count, int elementSize) {
        if (buffer.limit() % elementSize != 0) {
            throw new IllegalArgumentException("buffer length must be a multiple of " + elementSize);
        }
        if (buffer.limit() != count * elementSize) {
            throw new IllegalArgumentException("buffer length must be equal to count * elementSize");
        }
    }
}
