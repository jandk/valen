package be.twofold.valen.geometry;

import java.nio.*;

public record Mesh(
    FloatBuffer vertices,
    FloatBuffer normals,
    FloatBuffer texCoords,
    ShortBuffer indices
) {
}
