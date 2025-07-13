package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6AnimFrameSet(
    short firstROffset,
    short firstSOffset,
    short firstTOffset,
    short firstUOffset,
    short rangeROffset,
    short rangeSOffset,
    short rangeTOffset,
    short rangeUOffset,
    short RBitsOffset,
    short SBitsOffset,
    short TBitsOffset,
    short UBitsOffset,
    short nextROffset,
    short nextSOffset,
    short nextTOffset,
    short nextUOffset,
    short totalSize,
    short frameStart,
    short frameRange,
    String pad
) {
    public static Md6AnimFrameSet read(BinaryReader reader) throws IOException {
        short firstROffset = reader.readShort();
        short firstSOffset = reader.readShort();
        short firstTOffset = reader.readShort();
        short firstUOffset = reader.readShort();
        short rangeROffset = reader.readShort();
        short rangeSOffset = reader.readShort();
        short rangeTOffset = reader.readShort();
        short rangeUOffset = reader.readShort();
        short RBitsOffset = reader.readShort();
        short SBitsOffset = reader.readShort();
        short TBitsOffset = reader.readShort();
        short UBitsOffset = reader.readShort();
        short nextROffset = reader.readShort();
        short nextSOffset = reader.readShort();
        short nextTOffset = reader.readShort();
        short nextUOffset = reader.readShort();
        short totalSize = reader.readShort();
        short frameStart = reader.readShort();
        short frameRange = reader.readShort();
        String pad = new String(reader.readBytes(10));

        return new Md6AnimFrameSet(
            firstROffset,
            firstSOffset,
            firstTOffset,
            firstUOffset,
            rangeROffset,
            rangeSOffset,
            rangeTOffset,
            rangeUOffset,
            RBitsOffset,
            SBitsOffset,
            TBitsOffset,
            UBitsOffset,
            nextROffset,
            nextSOffset,
            nextTOffset,
            nextUOffset,
            totalSize,
            frameStart,
            frameRange,
            pad
        );
    }
}
