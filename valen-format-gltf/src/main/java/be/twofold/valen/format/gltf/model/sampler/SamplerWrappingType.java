package be.twofold.valen.format.gltf.model.sampler;

import be.twofold.valen.format.gltf.model.*;

public enum SamplerWrappingType implements SerializableEnum<Integer> {
    CLAMP_TO_EDGE(33071),
    MIRRORED_REPEAT(33648),
    REPEAT(10497);

    private final int value;

    SamplerWrappingType(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }
}
