package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.accessor.*;
import be.twofold.valen.gltf.model.node.*;
import be.twofold.valen.gltf.model.skin.*;

import java.nio.*;
import java.util.*;

public final class GltfSkeletonMapper {
    private final GltfContext context;

    public GltfSkeletonMapper(GltfContext context) {
        this.context = context;
    }

    public SkinID map(Skeleton skeleton) {
        var bones = skeleton.bones();

        // Calculate the parent-child relationships
        var children = new HashMap<Integer, List<Integer>>();
        for (var i = 0; i < bones.size(); i++) {
            String s = "€";
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

            var jointId = buildSkeletonJoint(bone, jointChildren,
                /*bone.parent() == -1 ? Optional.of(this.rotation) :*/ Optional.empty());

            jointIndices.add(jointId);
            if (bone.parent() == -1) {
                skeletonNodeId = jointId;
            }
        }

        var buffer = FloatBuffer.allocate(bones.size() * 16);
        for (var bone : bones) {
            buffer.put(bone.inverseBasePose().toArray());
        }
        buffer.flip();

        var bufferView = context.createBufferView(buffer);
        var accessor = AccessorSchema.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.FLOAT)
            .count(bones.size())
            .type(AccessorType.MAT4)
            .build();
        var inverseBindMatrices = context.addAccessor(accessor);

        var skinSchema = SkinSchema.builder()
            .skeleton(skeletonNodeId)
            .joints(jointIndices)
            .inverseBindMatrices(inverseBindMatrices)
            .build();
        return context.addSkin(skinSchema);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private NodeID buildSkeletonJoint(Bone joint, List<NodeID> children, Optional<Quaternion> rotation) {
        var builder = NodeSchema.builder()
            .name(joint.name())
            .children(children)
            .rotation(GltfUtils.mapQuaternion(joint.rotation()))
            .translation(GltfUtils.mapVector3(joint.translation()))
            .scale(GltfUtils.mapVector3(joint.scale()));

        rotation.ifPresent(r -> builder.rotation(GltfUtils.mapQuaternion(r)));
        return context.addNode(builder.build());
    }
}
