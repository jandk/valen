package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.gltf.*;
import be.twofold.valen.export.gltf.model.*;

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
        var skeletonNode = NodeId.of(-1);
        var jointIndices = new ArrayList<NodeId>();
        for (var i = 0; i < bones.size(); i++) {
            if (bones.get(i).parent() == -1) {
                skeletonNode = NodeId.of(offset + i);
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
            .componentType(AccessorComponentType.Float)
            .count(bones.size())
            .type(AccessorType.Matrix4)
            .build();
        var inverseBindMatrices = context.addAccessor(accessor);

        return SkinSchema.builder()
            .skeleton(skeletonNode)
            .joints(jointIndices)
            .inverseBindMatrices(inverseBindMatrices)
            .build();
    }

    private void buildSkeletonJoint(Bone joint, List<NodeId> children) {
        var node = NodeSchema.builder()
            .name(joint.name())
            .rotation(joint.rotation())
            .translation(joint.translation())
            .scale(joint.scale())
            .children(children)
            .build();
        context.addNode(node);
    }
}
