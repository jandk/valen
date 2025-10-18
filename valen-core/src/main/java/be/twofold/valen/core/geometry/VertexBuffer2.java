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
    Optional<Bytes> colors,
    int maximumInfluence
) {
    public VertexBuffer2 {
        Check.notNull(positions, "positions");
        Check.argument(positions.size() % 3 == 0, "positions.size() % 3 != 0");

        int vertexCount = positions.size() / 3;
        normals.ifPresent(fb -> check(fb.size(), vertexCount, 3));
        tangents.ifPresent(fb -> check(fb.size(), vertexCount, 4));
        for (var fb : texCoords) {
            check(fb.size(), vertexCount, 2);
        }
        joints.ifPresent(sb -> check(sb.size(), vertexCount, maximumInfluence));
        weights.ifPresent(fb -> check(fb.size(), vertexCount, maximumInfluence));
        colors.ifPresent(bb -> check(bb.size(), vertexCount, 4));
    }

    public int vertexCount() {
        return positions.size() / 3;
    }

    private void check(int length, int count, int elementSize) {
        Check.argument(length % elementSize == 0, "buffer length must be a multiple of elementSize");
        Check.argument(length == count * elementSize, "buffer length must be equal to count * elementSize");
    }
}
