package be.twofold.valen.export.cast.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.format.cast.*;

public final class CastSkeletonMapper {
    public long map(Skeleton skeleton, CastNode.Model modelNode) {
        var skeletonNode = modelNode.createSkeleton();
        for (var bone : skeleton.bones()) {
            mapBoneToNode(skeletonNode, bone);
        }
        return skeletonNode.hash();
    }

    private void mapBoneToNode(CastNode.Skeleton skeletonNode, Bone bone) {
        skeletonNode.createBone()
            .setName(bone.name())
            .setParentIndex(bone.parent())
            .setLocalRotation(CastUtils.mapQuaternion(bone.rotation()))
            .setLocalPosition(CastUtils.mapVector3(bone.translation()))
            .setScale(CastUtils.mapVector3(bone.scale()));
    }
}
