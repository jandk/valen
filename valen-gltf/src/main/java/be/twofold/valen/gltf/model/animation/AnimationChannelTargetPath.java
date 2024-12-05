package be.twofold.valen.gltf.model.animation;

import be.twofold.valen.gltf.model.*;

public enum AnimationChannelTargetPath implements ValueEnum<String> {
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
