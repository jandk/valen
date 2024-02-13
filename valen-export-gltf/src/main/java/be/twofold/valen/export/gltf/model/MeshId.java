package be.twofold.valen.export.gltf.model;

public final class MeshId extends AbstractId {
    private MeshId(int id) {
        super(id);
    }

    public static MeshId of(int id) {
        return new MeshId(id);
    }
}
