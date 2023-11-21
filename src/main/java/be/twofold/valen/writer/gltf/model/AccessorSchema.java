package be.twofold.valen.writer.gltf.model;

import be.twofold.valen.geometry.*;

import java.util.*;

public record AccessorSchema(
    int bufferView,
    AccessorComponentType componentType,
    int count,
    AccessorType type,
    Vector3 min,
    Vector3 max,
    Boolean normalized
) {
    public AccessorSchema {
        Objects.requireNonNull(componentType, "componentType must not be null");
        Objects.requireNonNull(type, "type must not be null");
    }
}
