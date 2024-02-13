package be.twofold.valen.export.gltf.model;

public final class SkinId extends AbstractId {
    private SkinId(int id) {
        super(id);
    }

    public static SkinId of(int id) {
        return new SkinId(id);
    }
}
