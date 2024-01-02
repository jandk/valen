package be.twofold.valen.export.gltf.model;

import be.twofold.valen.core.geometry.*;

public enum AccessorType {
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

    public static AccessorType from(ElementType type) {
        return switch (type) {
            case Scalar -> Scalar;
            case Vector2 -> Vector2;
            case Vector3 -> Vector3;
            case Vector4 -> Vector4;
            case Matrix2 -> Matrix2;
            case Matrix3 -> Matrix3;
            case Matrix4 -> Matrix4;
        };
    }

    public String getValue() {
        return value;
    }

    public int getSize() {
        return size;
    }
}
