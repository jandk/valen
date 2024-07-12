package be.twofold.valen.export.gltf.model;

public final class TextureId extends AbstractId {
    private TextureId(int id) {
        super(id);
    }

    public static TextureId of(int id) {
        return new TextureId(id);
    }
}
