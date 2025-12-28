package be.twofold.valen.core.geometry;

/**
 * Type of data in the buffer, for now the same as the GLTF type.
 */
public enum ElementType {
    SCALAR(1),
    VECTOR2(2),
    VECTOR3(3),
    VECTOR4(4),
    MATRIX2(4),
    MATRIX3(9),
    MATRIX4(16);

    private final int count;

    ElementType(int count) {
        this.count = count;
    }

    public int count() {
        return count;
    }
}
