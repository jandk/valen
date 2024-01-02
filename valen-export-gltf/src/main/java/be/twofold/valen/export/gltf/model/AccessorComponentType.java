package be.twofold.valen.export.gltf.model;

import be.twofold.valen.core.geometry.*;

public enum AccessorComponentType {
    SignedByte(5120, 1),
    UnsignedByte(5121, 1),
    SignedShort(5122, 2),
    UnsignedShort(5123, 2),
    UnsignedInt(5125, 4),
    Float(5126, 4);

    private final int id;
    private final int size;

    AccessorComponentType(int id, int size) {
        this.id = id;
        this.size = size;
    }

    public static AccessorComponentType from(ComponentType type) {
        return switch (type) {
            case Byte -> SignedByte;
            case UnsignedByte -> UnsignedByte;
            case Short -> SignedShort;
            case UnsignedShort -> UnsignedShort;
            case UnsignedInt -> UnsignedInt;
            case Float -> Float;
        };
    }

    public int getId() {
        return id;
    }

    public int getSize() {
        return size;
    }
}
