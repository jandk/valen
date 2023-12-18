package be.twofold.valen.reader.md6skl;

import be.twofold.valen.core.util.*;

public record Md6SkeletonHeader(
    short size,
    short numJoints,
    short numUserChannels,
    short parentTblCrc,
    short basePoseOffset,
    short inverseBasePoseOffset,
    short parentTblOffset,
    short lastChildTblOffset,
    short jointHandleTblOffset,
    short userChannelHandleTblOffset,
    short[] jointWeightOffsets,
    short[] userWeightOffsets,
    short extraJointTblOffset,
    short loadedDataSize,
    byte[] pad
) {
    public static Md6SkeletonHeader read(BetterBuffer buffer) {
        buffer.skip(4); // These are a copy of the size
        var size = buffer.getShort();
        var numJoints = buffer.getShort();
        var numUserChannels = buffer.getShort();
        var parentTblCrc = buffer.getShort();
        var basePoseOffset = buffer.getShort();
        var inverseBasePoseOffset = buffer.getShort();
        var parentTblOffset = buffer.getShort();
        var lastChildTblOffset = buffer.getShort();
        var jointHandleTblOffset = buffer.getShort();
        var userChannelHandleTblOffset = buffer.getShort();
        var jointWeightOffsets = buffer.getShorts(8);
        var userWeightOffsets = buffer.getShorts(8);
        var extraJointTblOffset = buffer.getShort();
        buffer.expectShort(0); // skelRemapTblOffset
        var loadedDataSize = buffer.getShort();
        var pad = buffer.getBytes(6);

        return new Md6SkeletonHeader(
            size,
            numJoints,
            numUserChannels,
            parentTblCrc,
            basePoseOffset,
            inverseBasePoseOffset,
            parentTblOffset,
            lastChildTblOffset,
            jointHandleTblOffset,
            userChannelHandleTblOffset,
            jointWeightOffsets,
            userWeightOffsets,
            extraJointTblOffset,
            loadedDataSize,
            pad
        );
    }

    public int numJoints8() {
        return numJoints + 7 & ~7;
    }
}
