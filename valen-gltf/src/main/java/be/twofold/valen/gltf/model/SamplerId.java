package be.twofold.valen.gltf.model;

public final class SamplerId extends AbstractId {
    private SamplerId(int id) {
        super(id);
    }

    public static SamplerId of(int id) {
        return new SamplerId(id);
    }
}
