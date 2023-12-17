package be.twofold.valen.export.gltf.model;

public enum BufferType {
    Position(AccessorComponentType.Float, AccessorType.VEC3, false),
    Normal(AccessorComponentType.Float, AccessorType.VEC3, false),
    Tangent(AccessorComponentType.Float, AccessorType.VEC4, false),
    TexCoordN(AccessorComponentType.Float, AccessorType.VEC2, false),
    ColorN(AccessorComponentType.UnsignedByte, AccessorType.VEC4, true),
    JointsN(AccessorComponentType.UnsignedByte, AccessorType.VEC4, false),
    WeightsN(AccessorComponentType.UnsignedByte, AccessorType.VEC4, true),
    Indices(AccessorComponentType.UnsignedShort, AccessorType.SCALAR, false),
    InverseBind(AccessorComponentType.Float, AccessorType.MAT4, false),
    KeyFrame(AccessorComponentType.Float, AccessorType.SCALAR, false),
    Rotation(AccessorComponentType.Float, AccessorType.VEC4, false),
    ScaleTranslation(AccessorComponentType.Float, AccessorType.VEC3, false);

    private final AccessorComponentType componentType;
    private final AccessorType dataType;
    private final boolean normalized;

    BufferType(AccessorComponentType componentType, AccessorType dataType, boolean normalized) {
        this.componentType = componentType;
        this.dataType = dataType;
        this.normalized = normalized;
    }

    public AccessorComponentType getComponentType() {
        return componentType;
    }

    public AccessorType getDataType() {
        return dataType;
    }

    public boolean isNormalized() {
        return normalized;
    }
}
