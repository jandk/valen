package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public record Mesh2(
    Optional<String> name,
    IntBuffer indices,
    FloatBuffer positions,
    FloatBuffer normals,
    FloatBuffer tangents,
    FloatBuffer texCoords,
    ShortBuffer joints,
    FloatBuffer weights,
    ByteBuffer colors,
    Optional<Material> material,
    List<BlendShape> blendShapes
) {
    public Mesh2 {
        Check.notNull(indices, "indices");
        if (indices.limit() % 3 != 0) {
            throw new IllegalArgumentException("indices must be a multiple of 3");
        }
    }
}
