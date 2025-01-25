package be.twofold.valen.gltf.model.accessor;

import be.twofold.valen.gltf.model.*;

public enum AccessorType implements SerializableEnum<String> {
    SCALAR(1),
    VEC2(2),
    VEC3(3),
    VEC4(4),
    MAT2(4),
    MAT3(9),
    MAT4(16);

    private final int size;

    AccessorType(int size) {
        this.size = size;
    }

    @Override
    public String value() {
        return name();
    }

    public int size() {
        return size;
    }
}
