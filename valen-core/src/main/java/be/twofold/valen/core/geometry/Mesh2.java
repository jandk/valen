package be.twofold.valen.core.geometry;

import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;

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
        Check.argument(faceBuffer.limit() % 3 == 0, "faceBuffer must be a multiple of 3");
    }
}
