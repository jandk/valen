package be.twofold.valen.export.gltf.model;

public final class MaterialId extends AbstractId {
    private MaterialId(int id) {
        super(id);
    }

    public static MaterialId of(int id) {
        return new MaterialId(id);
    }
}
