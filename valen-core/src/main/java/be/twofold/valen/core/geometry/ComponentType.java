package be.twofold.valen.core.geometry;

/**
 * Type of data component in the buffer, for now the same as the GLTF type.
 */
public enum ComponentType {
    Byte(1),
    UnsignedByte(1),
    Short(2),
    UnsignedShort(2),
    UnsignedInt(4),
    Float(4);

    private final int size;

    ComponentType(int size) {
        this.size = size;
    }

    public int size() {
        return size;
    }
}
