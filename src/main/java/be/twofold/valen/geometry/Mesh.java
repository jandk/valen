package be.twofold.valen.geometry;

import java.nio.*;

public record Mesh(
    FloatBuffer positions,
    FloatBuffer normals,
    FloatBuffer tangents,
    FloatBuffer texCoords,
    ByteBuffer weights,
    ByteBuffer colors,
    ShortBuffer indices
) {
}
