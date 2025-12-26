package be.twofold.valen.game.eternal.reader.md6skel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

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
    Shorts jointWeightOffsets,
    Shorts userWeightOffsets,
    short extraJointTblOffset,
    short loadedDataSize,
    Bytes pad
) {
    public static Md6SkelHeader read(BinarySource source) throws IOException {
        source.skip(4); // These are a copy of the compressedSize
        var size = source.readShort();
        var numJoints = source.readShort();
        var numUserChannels = source.readShort();
        var parentTblCrc = source.readShort();
        var basePoseOffset = source.readShort();
        var inverseBasePoseOffset = source.readShort();
        var parentTblOffset = source.readShort();
        var lastChildTblOffset = source.readShort();
        var jointHandleTblOffset = source.readShort();
        var userChannelHandleTblOffset = source.readShort();
        var jointWeightOffsets = source.readShorts(8);
        var userWeightOffsets = source.readShorts(8);
        var extraJointTblOffset = source.readShort();
        source.expectShort((short) 0); // skelRemapTblOffset
        var loadedDataSize = source.readShort();
        var pad = source.readBytes(6);

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
