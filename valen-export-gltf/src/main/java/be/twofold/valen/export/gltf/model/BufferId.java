package be.twofold.valen.export.gltf.model;

public final class BufferId extends AbstractId {
    private BufferId(int id) {
        super(id);
    }

    public static BufferId of(int id) {
        return new BufferId(id);
    }
}
