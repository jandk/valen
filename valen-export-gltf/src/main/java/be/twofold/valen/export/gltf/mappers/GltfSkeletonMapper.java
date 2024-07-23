package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.*;

import java.nio.*;
import java.util.*;

public final class GltfSkeletonMapper {
    private final GltfContext context;

    public GltfSkeletonMapper(GltfContext context) {
        this.context = context;
    }

    public SkinSchema map(Skeleton skeleton, int offset) {
        var bones = skeleton.bones();

        // Calculate the parent-child relationships
        var children = new HashMap<NodeId, List<NodeId>>();
        for (var i = 0; i < bones.size(); i++) {
            var bone = bones.get(i);
            children
                .computeIfAbsent(NodeId.of(bone.parent()), __ -> new ArrayList<>())
                .add(NodeId.of(offset + i));
        }

        // Build the skeleton
        var skeletonNode = -1;
        var jointIndices = new ArrayList<NodeId>();
        for (var i = 0; i < bones.size(); i++) {
            if (bones.get(i).parent() == -1) {
                skeletonNode = offset + i;
            }
            jointIndices.add(NodeId.of(offset + i));
            buildSkeletonJoint(bones.get(i), children.getOrDefault(NodeId.of(i), List.of()));
        }

        var buffer = FloatBuffer.allocate(bones.size() * 16);
        for (var bone : bones) {
            buffer.put(bone.inverseBasePose().toArray());
        }
        buffer.flip();

        var bufferView = context.createBufferView(buffer, buffer.limit() * 4, null);

        var accessor = AccessorSchema.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.FLOAT)
            .count(bones.size())
            .type(AccessorType.MAT4)
            .build();
        var inverseBindMatrices = context.addAccessor(accessor);

        return SkinSchema.builder()
            .skeleton(NodeId.of(skeletonNode))
            .joints(jointIndices)
            .inverseBindMatrices(inverseBindMatrices)
            .build();
    }

    private void buildSkeletonJoint(Bone joint, List<NodeId> children) {
        var node = NodeSchema.builder()
            .name(joint.name())
            .rotation(GltfUtils.mapQuaternion(joint.rotation()))
            .translation(GltfUtils.mapVector3(joint.translation()))
            .scale(GltfUtils.mapVector3(joint.scale()))
            .children(children)
            .build();
        context.addNode(node);
    }
}
