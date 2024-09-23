package be.twofold.valen.gltf.model.animation;

import be.twofold.valen.gltf.model.*;

public enum AnimationChannelTargetPath implements ValueEnum<String> {
    Translation("translation"),
    Rotation("rotation"),
    Scale("scale"),
    Weights("weights");

    private final String value;

    AnimationChannelTargetPath(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
