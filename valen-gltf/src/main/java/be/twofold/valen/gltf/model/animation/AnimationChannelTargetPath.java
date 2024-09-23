package be.twofold.valen.gltf.model.animation;

public enum AnimationChannelTargetPath {
    Translation("translation"),
    Rotation("rotation"),
    Scale("scale"),
    Weights("weights");

    private final String value;

    AnimationChannelTargetPath(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
