package be.twofold.valen.reader.md6anim;

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
    public static Md6AnimFrameSet read(DataSource source) throws IOException {
        short firstROffset = source.readShort();
        short firstSOffset = source.readShort();
        short firstTOffset = source.readShort();
        short firstUOffset = source.readShort();
        short rangeROffset = source.readShort();
        short rangeSOffset = source.readShort();
        short rangeTOffset = source.readShort();
        short rangeUOffset = source.readShort();
        short RBitsOffset = source.readShort();
        short SBitsOffset = source.readShort();
        short TBitsOffset = source.readShort();
        short UBitsOffset = source.readShort();
        short nextROffset = source.readShort();
        short nextSOffset = source.readShort();
        short nextTOffset = source.readShort();
        short nextUOffset = source.readShort();
        short totalSize = source.readShort();
        short frameStart = source.readShort();
        short frameRange = source.readShort();
        String pad = new String(source.readBytes(10));

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
