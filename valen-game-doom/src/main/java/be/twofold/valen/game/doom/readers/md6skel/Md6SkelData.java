package be.twofold.valen.game.doom.readers.md6skel;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Md6SkelData(
    short size,
    short numJoints,
    short numUserChannels,
    short parentTblCrc,
    short basePoseOffset,
    short inverseBasePoseOffset,
    short parentTblOffset,
    short jointHandleTblOffset,
    short userChannelHandleTblOffset,
    Shorts jointWeightOffsets,
    Shorts userWeightOffsets
) {
    private static final int MAX_WEIGHT_SETS = 8;

    public static Md6SkelData read(BinaryReader reader) throws IOException {
        var size = reader.readShort();
        var numJoints = reader.readShort();
        var numUserChannels = reader.readShort();
        var parentTblCrc = reader.readShort();
        var basePoseOffset = reader.readShort();
        var inverseBasePoseOffset = reader.readShort();
        var parentTblOffset = reader.readShort();
        var jointHandleTblOffset = reader.readShort();
        var userChannelHandleTblOffset = reader.readShort();
        var jointWeightOffsets = reader.readShorts(MAX_WEIGHT_SETS);
        var userWeightOffsets = reader.readShorts(MAX_WEIGHT_SETS);

        return new Md6SkelData(
            size,
            numJoints,
            numUserChannels,
            parentTblCrc,
            basePoseOffset,
            inverseBasePoseOffset,
            parentTblOffset,
            jointHandleTblOffset,
            userChannelHandleTblOffset,
            jointWeightOffsets,
            userWeightOffsets
        );
    }

    public int numJoints8() {
        return numJoints + 7 & ~7;
    }

    public int numUserChannels8() {
        return numUserChannels + 7 & ~7;
    }
}
