package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;

/**
 * @param totalSize               size of the idMD6AnimData class + all static anim data + all frameset data
 * @param size                    size of the idMD6AnimData class + all static anim data (includes frameset tables)
 * @param flags                   various flags indicating properties of the animation
 * @param numFrames               total number of frames in the entire animation
 * @param frameRate               frame rate of the animation
 * @param srcSkel                 source skeleton hierarchy crc
 * @param baseSkel                the base skeleton crc, this is different than the source skeleton when the animation has attachments
 * @param numFrameSets            total number of frame sets in this animation
 * @param frameSetTblOffset       offset to table for looking up a frameset number by animation frame
 * @param frameSetOffsetTblOffset offset to table for looking up a frameset offset by frameset number
 * @param constROffset            offset to table with constant joint rotations during this animation
 * @param constSOffset            offset to table with constant joint scales during this animation
 * @param constTOffset            offset to table with constant joint translations during this animation
 * @param constUOffset            offset to table with constant user channel values during this animation
 * @param nextSize                size of the frameset base data (each frameset uses the base data of the next frameset)
 * @param jointWeightsOffset      offset to table of joint weights mask for this animation. If 0, the joint weights are all 1 no table is stored.
 * @param startDelta              the initial delta of the origin
 * @param endDelta                the final delta of the origin
 */
public record Md6AnimData(
    int totalSize,
    short size,
    short flags,
    short numFrames,
    short frameRate,
    int srcSkel,
    int baseSkel,
    short numFrameSets,
    Shorts frameSetTblOffset,
    short frameSetOffsetTblOffset,
    short constROffset,
    short constSOffset,
    short constTOffset,
    short constUOffset,
    short nextSize,
    short jointWeightsOffset,
    short numStreamedFrameSets,
    Md6AnimCompressionStreamMethod streamMethod,
    float assetScale,
    Floats startDelta,
    Floats endDelta
) {
    public Md6AnimData {
        if (numStreamedFrameSets != 0 && streamMethod != Md6AnimCompressionStreamMethod.LODS) {
            Check.argument(numFrameSets == numStreamedFrameSets, "numFrameSets and numStreamedFrameSets must be equal");
        }
    }

    public static Md6AnimData read(BinarySource source) throws IOException {
        var totalSize = source.readInt();
        var size = source.readShort();
        var flags = source.readShort();
        var numFrames = source.readShort();
        var frameRate = source.readShort();
        var srcSkel = source.readInt();
        var baseSkel = source.readInt();
        var numFrameSets = source.readShort();
        var frameSetTblOffset = source.readShorts(2);
        var frameSetOffsetTblOffset = source.readShort();
        var constROffset = source.readShort();
        var constSOffset = source.readShort();
        var constTOffset = source.readShort();
        var constUOffset = source.readShort();
        var nextSize = source.readShort();
        var jointWeightsOffset = source.readShort();
        var numStreamedFrameSets = source.readShort();
        var streamMethod = Md6AnimCompressionStreamMethod.fromValue(source.readByte());
        source.expectByte((byte) 0); // pad
        var assetScale = source.readFloat();
        var startDelta = source.readFloats(12);
        var endDelta = source.readFloats(12);

        return new Md6AnimData(
            totalSize,
            size,
            flags,
            numFrames,
            frameRate,
            srcSkel,
            baseSkel,
            numFrameSets,
            frameSetTblOffset,
            frameSetOffsetTblOffset,
            constROffset,
            constSOffset,
            constTOffset,
            constUOffset,
            nextSize,
            jointWeightsOffset,
            numStreamedFrameSets,
            streamMethod,
            assetScale,
            startDelta,
            endDelta
        );
    }
}
