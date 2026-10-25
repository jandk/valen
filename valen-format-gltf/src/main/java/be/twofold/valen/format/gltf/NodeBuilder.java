package be.twofold.valen.format.gltf;

import be.twofold.valen.format.gltf.model.mesh.*;
import be.twofold.valen.format.gltf.model.node.*;
import be.twofold.valen.format.gltf.model.skin.*;
import be.twofold.valen.format.gltf.types.*;

import java.util.*;
import java.util.stream.*;

/**
 * Builds a {@code NodeSchema} tree using child builder references instead of node indices.
 * Node IDs are assigned on {@link #commit(GltfContext)}, allowing skins and animations to
 * resolve referenced joints after the tree is committed.
 */
public final class NodeBuilder {
    private final List<NodeBuilder> children = new ArrayList<>();
    private SkinID skin;
    private MeshID mesh;
    private Vec4 rotation;
    private Vec3 scale;
    private Vec3 translation;
    private String name;

    public NodeBuilder skin(SkinID skin) {
        this.skin = skin;
        return this;
    }

    public NodeBuilder mesh(MeshID mesh) {
        this.mesh = mesh;
        return this;
    }

    public NodeBuilder rotation(Vec4 rotation) {
        this.rotation = rotation;
        return this;
    }

    public NodeBuilder scale(Vec3 scale) {
        this.scale = scale;
        return this;
    }

    public NodeBuilder translation(Vec3 translation) {
        this.translation = translation;
        return this;
    }

    public NodeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public NodeBuilder addChild(NodeBuilder child) {
        children.add(Objects.requireNonNull(child, "child"));
        return this;
    }

    /**
     * Commits this subtree to {@code context}, returning the node IDs assigned to each builder.
     */
    public Map<NodeBuilder, NodeID> commit(GltfContext context) {
        var ids = new LinkedHashMap<NodeBuilder, NodeID>();
        commit(context, ids);
        return ids;
    }

    private NodeID commit(GltfContext context, Map<NodeBuilder, NodeID> ids) {
        var childIds = children.stream()
            .map(child -> child.commit(context, ids))
            .toList();

        var builder = ImmutableNode.builder().children(childIds);
        if (skin != null) {
            builder.skin(skin);
        }
        if (mesh != null) {
            builder.mesh(mesh);
        }
        if (rotation != null) {
            builder.rotation(rotation);
        }
        if (scale != null) {
            builder.scale(scale);
        }
        if (translation != null) {
            builder.translation(translation);
        }
        if (name != null) {
            builder.name(name);
        }

        var id = context.addNode(builder.build());
        ids.put(this, id);
        return id;
    }
}
