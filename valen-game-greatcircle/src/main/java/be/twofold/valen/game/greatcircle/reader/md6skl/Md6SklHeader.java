package be.twofold.valen.game.greatcircle.reader.md6skl;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6SklHeader(
    int size,
    int inverseBasePoseOffset,
    int basePoseOffset,
    int skeletonCrc,
    int userChannelCrc,
    int combinedCrc,
    short numJoints,
    short numUserChannels,
    short numRigControls,
    short animationMaskOffset,
    short parentTblOffset,
    short lastChildTblOffset,
    short jointHandleTblOffset,
    short userChannelHandleTblOffset,
    short rigControlHandleTblOffset,
    short[] jointWeightOffsets,
    short[] userWeightOffsets,
    byte[] pad
) {
    public static Md6SklHeader read(DataSource source) throws IOException {
        int size = source.readInt();
        int inverseBasePoseOffset = source.readInt();
        int basePoseOffset = source.readInt();
        int skeletonCrc = source.readInt();
        int userChannelCrc = source.readInt();
        int combinedCrc = source.readInt();
        short numJoints = source.readShort();
        short numUserChannels = source.readShort();
        short numRigControls = source.readShort();
        short animationMaskOffset = source.readShort();
        short parentTblOffset = source.readShort();
        short lastChildTblOffset = source.readShort();
        short jointHandleTblOffset = source.readShort();
        short userChannelHandleTblOffset = source.readShort();
        short rigControlHandleTblOffset = source.readShort();
        short[] jointWeightOffsets = source.readShorts(8);
        short[] userWeightOffsets = source.readShorts(8);
        byte[] pad = source.readBytes(6);

        return new Md6SklHeader(
            size,
            inverseBasePoseOffset,
            basePoseOffset,
            skeletonCrc,
            userChannelCrc,
            combinedCrc,
            numJoints,
            numUserChannels,
            numRigControls,
            animationMaskOffset,
            parentTblOffset,
            lastChildTblOffset,
            jointHandleTblOffset,
            userChannelHandleTblOffset,
            rigControlHandleTblOffset,
            jointWeightOffsets,
            userWeightOffsets,
            pad
        );
    }
}
