package be.twofold.valen.export.gltf.model;

import java.util.*;

public record MeshSchema(
    List<PrimitiveSchema> primitives
) {
    public MeshSchema {
        primitives = List.copyOf(primitives);
    }
}
