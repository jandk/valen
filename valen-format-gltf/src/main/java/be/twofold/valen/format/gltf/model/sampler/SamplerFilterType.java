package be.twofold.valen.format.gltf.model.sampler;

import be.twofold.valen.format.gltf.model.*;

public enum SamplerFilterType implements SerializableEnum<Integer> {
    NEAREST(9728),
    LINEAR(9729),
    NEAREST_MIPMAP_NEAREST(9984),
    LINEAR_MIPMAP_NEAREST(9985),
    NEAREST_MIPMAP_LINEAR(9986),
    LINEAR_MIPMAP_LINEAR(9987);

    private final int value;

    SamplerFilterType(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }
}
