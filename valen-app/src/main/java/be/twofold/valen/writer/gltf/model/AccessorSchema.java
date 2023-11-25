package be.twofold.valen.writer.gltf.model;

import be.twofold.valen.core.util.*;

public record AccessorSchema(
    int bufferView,
    AccessorComponentType componentType,
    int count,
    AccessorType type,
    float[] min,
    float[] max,
    Boolean normalized
) {
    public AccessorSchema {
        Check.notNull(componentType, "componentType must not be null");
        Check.notNull(type, "type must not be null");
    }
}
