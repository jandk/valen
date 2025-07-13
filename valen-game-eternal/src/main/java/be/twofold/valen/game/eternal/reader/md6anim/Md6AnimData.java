package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6AnimData(
    int totalSize,
    short size,
    short flags,
    short numFrames,
    short frameRate,
    short numFrameSets,
    short frameSetTblOffset,
    short frameSetOffsetTblOffset,
    short constROffset,
    short constSOffset,
    short constTOffset,
    short constUOffset,
    short nextSize,
    short jointWeightsOffset,
    float[] startDelta,
    float[] endDelta
) {
    public static Md6AnimData read(BinaryReader reader) throws IOException {
        reader.expectLong(0);
        int totalSize = reader.readInt();
        short size = reader.readShort();
        short flags = reader.readShort();
        short numFrames = reader.readShort();
        short frameRate = reader.readShort();
        short numFrameSets = reader.readShort();
        short frameSetTblOffset = reader.readShort();
        short frameSetOffsetTblOffset = reader.readShort();
        short constROffset = reader.readShort();
        short constSOffset = reader.readShort();
        short constTOffset = reader.readShort();
        short constUOffset = reader.readShort();
        short nextSize = reader.readShort();
        short jointWeightsOffset = reader.readShort();
        reader.expectShort((short) 0);
        float[] startDelta = reader.readFloats(12);
        float[] endDelta = reader.readFloats(12);
        reader.expectLong(0);

        return new Md6AnimData(
            totalSize,
            size,
            flags,
            numFrames,
            frameRate,
            numFrameSets,
            frameSetTblOffset,
            frameSetOffsetTblOffset,
            constROffset,
            constSOffset,
            constTOffset,
            constUOffset,
            nextSize,
            jointWeightsOffset,
            startDelta,
            endDelta
        );
    }
}
