package be.twofold.valen.reader.md6anim;

import be.twofold.valen.*;

import java.nio.charset.*;

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
    public static Md6AnimFrameSet read(BetterBuffer buffer) {
        short firstROffset = buffer.getShort();
        short firstSOffset = buffer.getShort();
        short firstTOffset = buffer.getShort();
        short firstUOffset = buffer.getShort();
        short rangeROffset = buffer.getShort();
        short rangeSOffset = buffer.getShort();
        short rangeTOffset = buffer.getShort();
        short rangeUOffset = buffer.getShort();
        short RBitsOffset = buffer.getShort();
        short SBitsOffset = buffer.getShort();
        short TBitsOffset = buffer.getShort();
        short UBitsOffset = buffer.getShort();
        short nextROffset = buffer.getShort();
        short nextSOffset = buffer.getShort();
        short nextTOffset = buffer.getShort();
        short nextUOffset = buffer.getShort();
        short totalSize = buffer.getShort();
        short frameStart = buffer.getShort();
        short frameRange = buffer.getShort();
        String pad = new String(buffer.getBytes(10), StandardCharsets.US_ASCII);

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
