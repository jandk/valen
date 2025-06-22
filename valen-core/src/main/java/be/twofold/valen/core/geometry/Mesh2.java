package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;

import java.nio.*;
import java.util.*;

public record Mesh2(
    Optional<String> name,
    IntBuffer faceBuffer,
    VertexBuffer2 vertexBuffer,
    Optional<Material> material,
    List<BlendShape> blendShapes
) {
    public Mesh2 {
        if (faceBuffer.limit() % 3 != 0) { // implicit null-check
            throw new IllegalArgumentException("faceBuffer must be a multiple of 3");
        }
    }
}
