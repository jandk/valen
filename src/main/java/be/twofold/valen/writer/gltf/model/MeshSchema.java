package be.twofold.valen.writer.gltf.model;

import java.util.*;

public record MeshSchema(
    List<PrimitiveSchema> primitives
) {
    public MeshSchema {
        primitives = List.copyOf(primitives);
    }
}
