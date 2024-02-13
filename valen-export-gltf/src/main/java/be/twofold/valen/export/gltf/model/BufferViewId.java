package be.twofold.valen.export.gltf.model;

public final class BufferViewId extends AbstractId {
    private BufferViewId(int id) {
        super(id);
    }

    public static BufferViewId of(int id) {
        return new BufferViewId(id);
    }
}
