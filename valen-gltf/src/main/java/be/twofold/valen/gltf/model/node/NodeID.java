package be.twofold.valen.gltf.model.node;

import be.twofold.valen.gltf.model.*;

public final class NodeID extends GltfID implements Comparable<NodeID> {
    private NodeID(int id) {
        super(id);
    }

    public static NodeID of(int id) {
        return new NodeID(id);
    }

    public NodeID add(int offset) {
        return new NodeID(id() + offset);
    }

    @Override
    public int compareTo(NodeID o) {
        return Integer.compare(id(), o.id());
    }
}
