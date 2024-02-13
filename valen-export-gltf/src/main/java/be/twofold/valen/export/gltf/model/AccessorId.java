package be.twofold.valen.export.gltf.model;

public final class AccessorId extends AbstractId {
    private AccessorId(int id) {
        super(id);
    }

    public static AccessorId of(int id) {
        return new AccessorId(id);
    }
}
