package be.twofold.valen.gltf.model.accessor;

import be.twofold.valen.gltf.model.*;

public enum AccessorType implements ValueEnum<String> {
    Scalar("SCALAR", 1),
    Vector2("VEC2", 2),
    Vector3("VEC3", 3),
    Vector4("VEC4", 4),
    Matrix2("MAT2", 4),
    Matrix3("MAT3", 9),
    Matrix4("MAT4", 16);

    private final String value;
    private final int size;

    AccessorType(String value, int size) {
        this.value = value;
        this.size = size;
    }

    @Override
    public String value() {
        return value;
    }

    public int size() {
        return size;
    }
}
