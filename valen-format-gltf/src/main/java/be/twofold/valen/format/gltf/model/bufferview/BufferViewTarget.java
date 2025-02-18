package be.twofold.valen.format.gltf.model.bufferview;

import be.twofold.valen.format.gltf.model.*;

public enum BufferViewTarget implements SerializableEnum<Integer> {
    ARRAY_BUFFER(34962),
    ELEMENT_ARRAY_BUFFER(34963);

    private final int value;

    BufferViewTarget(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }
}
