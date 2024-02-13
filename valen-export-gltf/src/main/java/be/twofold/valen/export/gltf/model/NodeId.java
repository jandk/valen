package be.twofold.valen.export.gltf.model;

public final class NodeId extends AbstractId {
    private NodeId(int id) {
        super(id);
    }

    public static NodeId of(int id) {
        return new NodeId(id);
    }

    public NodeId add(int offset) {
        return new NodeId(getId() + offset);
    }
}
