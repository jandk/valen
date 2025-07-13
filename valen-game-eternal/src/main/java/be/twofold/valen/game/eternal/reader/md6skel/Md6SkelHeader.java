package be.twofold.valen.game.eternal.reader.md6skel;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

public record Md6SkelHeader(
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
    public static Md6SkelHeader read(BinaryReader reader) throws IOException {
        reader.skip(4); // These are a copy of the compressedSize
        var size = reader.readShort();
        var numJoints = reader.readShort();
        var numUserChannels = reader.readShort();
        var parentTblCrc = reader.readShort();
        var basePoseOffset = reader.readShort();
        var inverseBasePoseOffset = reader.readShort();
        var parentTblOffset = reader.readShort();
        var lastChildTblOffset = reader.readShort();
        var jointHandleTblOffset = reader.readShort();
        var userChannelHandleTblOffset = reader.readShort();
        var jointWeightOffsets = reader.readShorts(8);
        var userWeightOffsets = reader.readShorts(8);
        var extraJointTblOffset = reader.readShort();
        reader.expectShort((short) 0); // skelRemapTblOffset
        var loadedDataSize = reader.readShort();
        var pad = reader.readBytes(6);

        return new Md6SkelHeader(
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
