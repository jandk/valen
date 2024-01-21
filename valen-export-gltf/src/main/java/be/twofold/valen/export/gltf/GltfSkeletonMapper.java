package be.twofold.valen.export.gltf;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.export.gltf.model.*;

import java.nio.*;
import java.util.*;

final class GltfSkeletonMapper {
    private final GltfContext context;

    GltfSkeletonMapper(GltfContext context) {
        this.context = context;
    }

    SkinSchema map(Skeleton skeleton, int offset) {
        var bones = skeleton.bones();

        // Calculate the parent-child relationships
        var children = new HashMap<Integer, List<Integer>>();
        for (var i = 0; i < bones.size(); i++) {
            var bone = bones.get(i);
            children
                .computeIfAbsent(bone.parent(), __ -> new ArrayList<>())
                .add(offset + i);
        }

        // Build the skeleton
        var skeletonNode = -1;
        var jointIndices = new ArrayList<Integer>();
        for (var i = 0; i < bones.size(); i++) {
            if (bones.get(i).parent() == -1) {
                skeletonNode = offset + i;
            }
            jointIndices.add(offset + i);
            buildSkeletonJoint(bones.get(i), children.getOrDefault(i, List.of()));
        }

        var buffer = FloatBuffer.allocate(bones.size() * 16);
        for (var bone : bones) {
            buffer.put(bone.inverseBasePose().toArray());
        }
        buffer.flip();

        var bufferView = context.createBufferView(buffer, buffer.limit() * 4, null);
        var accessor = new AccessorSchema(
            bufferView,
            AccessorComponentType.Float,
            bones.size(),
            AccessorType.Matrix4,
            null,
            null,
            null
        );

        var inverseBindMatrices = context.addAccessor(accessor);

        // Build the skin
        context.setSkeletonNode(skeletonNode);
        return new SkinSchema(
            skeletonNode,
            jointIndices,
            inverseBindMatrices
        );
    }

    private void buildSkeletonJoint(Bone joint, List<Integer> children) {
        var node = NodeSchema.buildSkeletonNode(
            joint.name(),
            joint.rotation(),
            joint.translation(),
            joint.scale(),
            children.isEmpty() ? null : children
        );
        context.addNode(node);
    }

}
