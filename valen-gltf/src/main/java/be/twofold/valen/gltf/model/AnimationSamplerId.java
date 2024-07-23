package be.twofold.valen.gltf.model;

public final class AnimationSamplerId extends AbstractId {
    private AnimationSamplerId(int id) {
        super(id);
    }

    public static AnimationSamplerId of(int id) {
        return new AnimationSamplerId(id);
    }
}
