package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.accessor.*;
import be.twofold.valen.format.gltf.model.node.*;
import be.twofold.valen.format.gltf.model.skin.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class GltfSkeletonMapper {
    private final GltfContext context;

    public GltfSkeletonMapper(GltfContext context) {
        this.context = context;
    }

    /**
     * Maps a skeleton to a glTF skin and returns it together with the node ID of its root joint, so the
     * caller can parent it into the model's node tree. The joints are plain nodes in that tree, which is
     * what lets the skeleton merge into the model rather than living under a dedicated root. Per the spec
     * {@code skin.skeleton} points at that root joint (the closest common root of the joints hierarchy).
     */
    public MappedSkin mapSkin(Skeleton skeleton) throws IOException {
        var bones = skeleton.bones();

        var jointBuilders = bones.stream()
            .map(bone -> new NodeBuilder()
                .name(bone.name())
                .rotation(GltfUtils.mapQuaternion(bone.rotation()))
                .scale(GltfUtils.mapVector3(bone.scale()))
                .translation(GltfUtils.mapVector3(bone.translation())))
            .toList();

        NodeBuilder root = null;
        for (var i = 0; i < bones.size(); i++) {
            var parent = bones.get(i).parent();
            if (parent == -1) {
                if (root != null) {
                    throw new IllegalStateException("Skeleton has multiple roots");
                }
                root = jointBuilders.get(i);
            } else {
                jointBuilders.get(parent).addChild(jointBuilders.get(i));
            }
        }
        if (root == null) {
            throw new IllegalStateException("Skeleton has no roots");
        }

        var ids = commitTree(root);

        var buffer = FloatBuffer.allocate(bones.size() * 16);
        for (var bone : bones) {
            bone.inverseBasePose().toBuffer(buffer);
        }
        buffer.flip();

        var bufferView = context.createBufferView(buffer, null);
        var accessor = ImmutableAccessor.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.FLOAT)
            .count(bones.size())
            .type(AccessorType.MAT4)
            .build();
        var inverseBindMatrices = context.addAccessor(accessor);

        var jointNodeIDs = jointBuilders.stream()
            .map(ids::get)
            .toList();
        var rootJoint = ids.get(root);
        var skinSchema = ImmutableSkin.builder()
            .joints(jointNodeIDs)
            .inverseBindMatrices(inverseBindMatrices)
            .skeleton(rootJoint)
            .build();
        var skinID = context.addSkin(skinSchema);

        return new MappedSkin(skinID, rootJoint, jointNodeIDs);
    }

    private Map<NodeBuilder, NodeID> commitTree(NodeBuilder root) {
        return root.commit(context);
    }

    public record MappedSkin(
        SkinID skin,
        NodeID rootJoint,
        List<NodeID> jointNodeIDs
    ) {
    }
}
