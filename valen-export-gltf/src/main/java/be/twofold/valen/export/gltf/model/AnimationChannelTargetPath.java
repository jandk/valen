package be.twofold.valen.export.gltf.model;

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
