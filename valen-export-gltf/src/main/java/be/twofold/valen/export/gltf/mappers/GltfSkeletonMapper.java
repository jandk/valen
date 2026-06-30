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

    public SkinID map(Skeleton skeleton) throws IOException {
        var bones = skeleton.bones();

        // Calculate the parent-child relationships
        var children = new HashMap<Integer, List<Integer>>();
        for (var i = 0; i < bones.size(); i++) {
            children
                .computeIfAbsent(bones.get(i).parent(), _ -> new ArrayList<>())
                .add(i);
        }

        // Build the skeleton
        var baseNodeId = context.nextNodeId();
        var skeletonNodeId = (NodeID) null;
        var jointIndices = new ArrayList<NodeID>();
        for (var i = 0; i < bones.size(); i++) {
            var bone = bones.get(i);
            var jointChildren = children.getOrDefault(i, List.of()).stream()
                .map(baseNodeId::add)
                .toList();

            var jointId = buildSkeletonJoint(bone, jointChildren);

            jointIndices.add(jointId);
            if (bone.parent() == -1) {
                skeletonNodeId = jointId;
            }
        }

        // The up-axis rotation can't live on the skinned mesh node (its transform is ignored) nor on
        // the root joint (that corrupts the bind pose and gets overwritten on the first animation
        // frame). Put it on a wrapper node above the root joint, so it flows into every joint's world
        // transform while the joints keep their original bind rotations. The wrapper is added after the
        // joints so the joint node ids stay contiguous (animation channels target them by bone index).
        var rootNodeId = context.addNode(ImmutableNode.builder()
            .rotation(GltfUtils.mapQuaternion(skeleton.upAxis().rotateTo(Axis.Y)))
            .addChildren(skeletonNodeId)
            .build());

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

        var skinSchema = ImmutableSkin.builder()
            .skeleton(rootNodeId)
            .joints(jointIndices)
            .inverseBindMatrices(inverseBindMatrices)
            .build();
        return context.addSkin(skinSchema);
    }

    private NodeID buildSkeletonJoint(Bone joint, List<NodeID> children) {
        return context.addNode(ImmutableNode.builder()
            .name(joint.name())
            .children(children)
            .rotation(GltfUtils.mapQuaternion(joint.rotation()))
            .translation(GltfUtils.mapVector3(joint.translation()))
            .scale(GltfUtils.mapVector3(joint.scale()))
            .build());
    }
}
