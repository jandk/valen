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
        short size = buffer.getShort();
        short numJoints = buffer.getShort();
        short numUserChannels = buffer.getShort();
        short parentTblCrc = buffer.getShort();
        short basePoseOffset = buffer.getShort();
        short inverseBasePoseOffset = buffer.getShort();
        short parentTblOffset = buffer.getShort();
        short lastChildTblOffset = buffer.getShort();
        short jointHandleTblOffset = buffer.getShort();
        short userChannelHandleTblOffset = buffer.getShort();
        short[] jointWeightOffsets = buffer.getShorts(8);
        short[] userWeightOffsets = buffer.getShorts(8);
        short extraJointTblOffset = buffer.getShort();
        buffer.expectShort(0); // skelRemapTblOffset
        short loadedDataSize = buffer.getShort();
        byte[] pad = buffer.getBytes(6);

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
