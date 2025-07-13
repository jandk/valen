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
    public static Md6AnimFrameSet read(BinaryReader reader) throws IOException {
        var firstROffset = reader.readShort();
        var firstSOffset = reader.readShort();
        var firstTOffset = reader.readShort();
        var firstUOffset = reader.readShort();
        var rangeROffset = reader.readShort();
        var rangeSOffset = reader.readShort();
        var rangeTOffset = reader.readShort();
        var rangeUOffset = reader.readShort();
        var RBitsOffset = reader.readShort();
        var SBitsOffset = reader.readShort();
        var TBitsOffset = reader.readShort();
        var UBitsOffset = reader.readShort();
        var nextROffset = reader.readShort();
        var nextSOffset = reader.readShort();
        var nextTOffset = reader.readShort();
        var nextUOffset = reader.readShort();
        var totalSize = reader.readShort();
        var frameStart = reader.readShort();
        var frameRange = reader.readShort();
        var lodIndex = reader.readByte();
        for (int i = 0; i < 9; i++) {
            reader.expectByte((byte) 0);
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
