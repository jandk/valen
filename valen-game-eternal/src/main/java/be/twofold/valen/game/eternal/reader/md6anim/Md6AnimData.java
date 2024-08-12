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
    public static Md6AnimData read(DataSource source) throws IOException {
        source.expectLong(0);
        int totalSize = source.readInt();
        short size = source.readShort();
        short flags = source.readShort();
        short numFrames = source.readShort();
        short frameRate = source.readShort();
        short numFrameSets = source.readShort();
        short frameSetTblOffset = source.readShort();
        short frameSetOffsetTblOffset = source.readShort();
        short constROffset = source.readShort();
        short constSOffset = source.readShort();
        short constTOffset = source.readShort();
        short constUOffset = source.readShort();
        short nextSize = source.readShort();
        short jointWeightsOffset = source.readShort();
        source.expectShort((short) 0);
        float[] startDelta = source.readFloats(12);
        float[] endDelta = source.readFloats(12);
        source.expectLong(0);

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
