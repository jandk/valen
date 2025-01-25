package be.twofold.valen.format.gltf.model.accessor;

import be.twofold.valen.format.gltf.model.*;

public enum AccessorComponentType implements SerializableEnum<Integer> {
    BYTE(5120, 1),
    UNSIGNED_BYTE(5121, 1),
    SHORT(5122, 2),
    UNSIGNED_SHORT(5123, 2),
    UNSIGNED_INT(5125, 4),
    FLOAT(5126, 4);

    private final int value;
    private final int size;

    AccessorComponentType(int value, int size) {
        this.value = value;
        this.size = size;
    }

    @Override
    public Integer value() {
        return value;
    }

    public int size() {
        return size;
    }
}
