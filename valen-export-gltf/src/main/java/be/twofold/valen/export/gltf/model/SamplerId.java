package be.twofold.valen.export.gltf.model;

public final class SamplerId extends AbstractId {
    private SamplerId(int id) {
        super(id);
    }

    public static SamplerId of(int id) {
        return new SamplerId(id);
    }
}
