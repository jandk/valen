package be.twofold.valen.game.greatcircle.reader.md6skl;

import be.twofold.valen.core.io.BinaryReader;

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
    public static Md6SklHeader read(BinaryReader reader) throws IOException {
        int size = reader.readInt();
        int inverseBasePoseOffset = reader.readInt();
        int basePoseOffset = reader.readInt();
        int skeletonCrc = reader.readInt();
        int userChannelCrc = reader.readInt();
        int combinedCrc = reader.readInt();
        short numJoints = reader.readShort();
        short numUserChannels = reader.readShort();
        short numRigControls = reader.readShort();
        short animationMaskOffset = reader.readShort();
        short parentTblOffset = reader.readShort();
        short lastChildTblOffset = reader.readShort();
        short jointHandleTblOffset = reader.readShort();
        short userChannelHandleTblOffset = reader.readShort();
        short rigControlHandleTblOffset = reader.readShort();
        short[] jointWeightOffsets = reader.readShorts(8);
        short[] userWeightOffsets = reader.readShorts(8);
        byte[] pad = reader.readBytes(6);

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
