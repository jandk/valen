package be.twofold.valen.core.geometry;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.util.*;

public record VertexBuffer2(
    Floats positions,
    Optional<Floats> normals,
    Optional<Floats> tangents,
    List<Floats> texCoords,
    Optional<Shorts> joints,
    Optional<Floats> weights,
    Optional<Bytes> colors
) {
    public VertexBuffer2 {
        Check.notNull(positions, "positions");
        normals.ifPresent(fb -> check(fb.size(), positions.size(), 3));
        tangents.ifPresent(fb -> check(fb.size(), positions.size(), 4));
        for (var fb : texCoords) {
            check(fb.size(), positions.size(), 2);
        }
        joints.ifPresent(sb -> check(sb.size(), positions.size(), 4));
        weights.ifPresent(fb -> check(fb.size(), positions.size(), 4));
        colors.ifPresent(bb -> check(bb.size(), positions.size(), 4));
    }

    private void check(int length, int count, int elementSize) {
        Check.argument(length % elementSize == 0, "buffer length must be a multiple of elementSize");
        Check.argument(length == count * elementSize, "buffer length must be equal to count * elementSize");
    }
}
