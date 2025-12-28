package be.twofold.valen.format.gltf.model.animation;

import be.twofold.valen.format.gltf.model.*;

public enum AnimationChannelTargetPath implements SerializableEnum<String> {
    TRANSLATION("translation"),
    ROTATION("rotation"),
    SCALE("scale"),
    WEIGHTS("weights");

    private final String value;

    AnimationChannelTargetPath(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
