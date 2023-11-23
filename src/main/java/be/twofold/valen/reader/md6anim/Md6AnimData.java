package be.twofold.valen.reader.md6anim;

import be.twofold.valen.*;

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
    public static Md6AnimData read(BetterBuffer buffer) {
        buffer.expectLong(0);
        int totalSize = buffer.getInt();
        short size = buffer.getShort();
        short flags = buffer.getShort();
        short numFrames = buffer.getShort();
        short frameRate = buffer.getShort();
        short numFrameSets = buffer.getShort();
        short frameSetTblOffset = buffer.getShort();
        short frameSetOffsetTblOffset = buffer.getShort();
        short constROffset = buffer.getShort();
        short constSOffset = buffer.getShort();
        short constTOffset = buffer.getShort();
        short constUOffset = buffer.getShort();
        short nextSize = buffer.getShort();
        short jointWeightsOffset = buffer.getShort();
        buffer.expectShort(0);
        float[] startDelta = new float[12];
        for (int i = 0; i < 12; i++) {
            startDelta[i] = buffer.getFloat();
        }
        float[] endDelta = new float[12];
        for (int i = 0; i < 12; i++) {
            endDelta[i] = buffer.getFloat();
        }
        buffer.expectLong(0);

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
