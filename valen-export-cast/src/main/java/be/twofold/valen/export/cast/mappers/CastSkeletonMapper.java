package be.twofold.valen.export.cast.mappers;

import be.twofold.tinycast.*;
import be.twofold.valen.core.geometry.*;

public final class CastSkeletonMapper {
    public long map(Skeleton skeleton, CastNodes.Model modelNode) {
        var skeletonNode = modelNode.createSkeleton();
        for (var bone : skeleton.bones()) {
            mapBoneToNode(skeletonNode, bone);
        }
        return skeletonNode.getHash();
    }

    private void mapBoneToNode(CastNodes.Skeleton skeletonNode, Bone bone) {
        skeletonNode.createBone()
            .setName(bone.name())
            .setParentIndex(bone.parent())
            .setLocalRotation(CastUtils.mapQuaternion(bone.rotation()))
            .setLocalPosition(CastUtils.mapVector3(bone.translation()))
            .setScale(CastUtils.mapVector3(bone.scale()));
    }
}
