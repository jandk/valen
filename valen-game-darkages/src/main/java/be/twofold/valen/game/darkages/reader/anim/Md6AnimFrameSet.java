package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;

import java.io.*;

/**
 * @param firstROffset offset to compressed base rotation keys
 * @param firstSOffset offset to compressed base scale keys
 * @param firstTOffset offset to compressed base translation keys
 * @param firstUOffset offset to compressed base user channel keys
 * @param rangeROffset offset to compressed additional rotation keys
 * @param rangeSOffset offset to compressed additional scale keys
 * @param rangeTOffset offset to compressed additional translation keys
 * @param rangeUOffset offset to compressed additional user channel keys
 * @param RBitsOffset  offset to rotation frame offset bits
 * @param SBitsOffset  offset to scale frame offset bits
 * @param TBitsOffset  offset to translation frame offset bits
 * @param UBitsOffset  offset to user channel frame offset bits
 * @param nextROffset  offset to compressed base rotation keys of next frame set
 * @param nextSOffset  offset to compressed base scale keys of next frame set
 * @param nextTOffset  offset to compressed base translation keys of next frame set
 * @param nextUOffset  offset to compressed base user channel keys of next frame set
 * @param totalSize    total size of the frame set
 * @param frameStart   first frame of the animation this frame set encodes
 * @param frameRange   number of frames this set encodes
 * @param lodIndex     lod this frameset is for
 */
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
    byte lodIndex
) {
    public static Md6AnimFrameSet read(BinarySource source) throws IOException {
        var firstROffset = source.readShort();
        var firstSOffset = source.readShort();
        var firstTOffset = source.readShort();
        var firstUOffset = source.readShort();
        var rangeROffset = source.readShort();
        var rangeSOffset = source.readShort();
        var rangeTOffset = source.readShort();
        var rangeUOffset = source.readShort();
        var RBitsOffset = source.readShort();
        var SBitsOffset = source.readShort();
        var TBitsOffset = source.readShort();
        var UBitsOffset = source.readShort();
        var nextROffset = source.readShort();
        var nextSOffset = source.readShort();
        var nextTOffset = source.readShort();
        var nextUOffset = source.readShort();
        var totalSize = source.readShort();
        var frameStart = source.readShort();
        var frameRange = source.readShort();
        var lodIndex = source.readByte();
        for (int i = 0; i < 9; i++) {
            source.expectByte((byte) 0);
        }

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
            lodIndex
        );
    }
}
