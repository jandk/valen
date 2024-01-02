package be.twofold.valen.core.geometry;

/**
 * Type of data in the buffer, for now the same as the GLTF type.
 */
public enum ElementType {
    Scalar(1),
    Vector2(2),
    Vector3(3),
    Vector4(4),
    Matrix2(4),
    Matrix3(9),
    Matrix4(16);

    private final int size;

    ElementType(int size) {
        this.size = size;
    }

    public int size() {
        return size;
    }
}
