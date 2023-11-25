package be.twofold.valen.core.geometry;

import java.nio.*;

public record Mesh(
    FloatBuffer positions,
    FloatBuffer normals,
    FloatBuffer tangents,
    FloatBuffer texCoords,
    ByteBuffer colors,
    ByteBuffer joints,
    ByteBuffer weights,
    ShortBuffer indices
) {
}
