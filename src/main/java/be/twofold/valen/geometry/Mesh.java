package be.twofold.valen.geometry;

import java.nio.*;

public record Mesh(
    FloatBuffer vertices,
    FloatBuffer normals,
    FloatBuffer tangents,
    FloatBuffer texCoords,
    ShortBuffer indices
) {
}
