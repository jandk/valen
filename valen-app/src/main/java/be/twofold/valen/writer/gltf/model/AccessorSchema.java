package be.twofold.valen.writer.gltf.model;

import java.util.*;

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
        Objects.requireNonNull(componentType, "componentType must not be null");
        Objects.requireNonNull(type, "type must not be null");
    }
}
