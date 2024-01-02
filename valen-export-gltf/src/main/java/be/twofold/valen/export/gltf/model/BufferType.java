package be.twofold.valen.export.gltf.model;

public enum BufferType {
    InverseBind(AccessorComponentType.Float, AccessorType.Matrix4),
    KeyFrame(AccessorComponentType.Float, AccessorType.Scalar),
    Rotation(AccessorComponentType.Float, AccessorType.Vector4),
    ScaleTranslation(AccessorComponentType.Float, AccessorType.Vector3);

    private final AccessorComponentType componentType;
    private final AccessorType dataType;

    BufferType(AccessorComponentType componentType, AccessorType dataType) {
        this.componentType = componentType;
        this.dataType = dataType;
    }

    public AccessorComponentType getComponentType() {
        return componentType;
    }

    public AccessorType getDataType() {
        return dataType;
    }
}
