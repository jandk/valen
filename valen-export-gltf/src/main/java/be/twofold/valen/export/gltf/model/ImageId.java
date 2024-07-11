package be.twofold.valen.export.gltf.model;

public final class ImageId extends AbstractId {
    private ImageId(int id) {
        super(id);
    }

    public static ImageId of(int id) {
        return new ImageId(id);
    }
}
