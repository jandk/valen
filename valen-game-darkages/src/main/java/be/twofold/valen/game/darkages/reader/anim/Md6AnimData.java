package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

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
    short[] frameSetTblOffset,
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
    float[] startDelta,
    float[] endDelta
) {
    public Md6AnimData {
        if (numStreamedFrameSets != 0 && streamMethod != Md6AnimCompressionStreamMethod.LODS) {
            Check.argument(numFrameSets == numStreamedFrameSets, "numFrameSets and numStreamedFrameSets must be equal");
        }
    }

    public static Md6AnimData read(BinaryReader reader) throws IOException {
        var totalSize = reader.readInt();
        var size = reader.readShort();
        var flags = reader.readShort();
        var numFrames = reader.readShort();
        var frameRate = reader.readShort();
        var srcSkel = reader.readInt();
        var baseSkel = reader.readInt();
        var numFrameSets = reader.readShort();
        var frameSetTblOffset = reader.readShorts(2);
        var frameSetOffsetTblOffset = reader.readShort();
        var constROffset = reader.readShort();
        var constSOffset = reader.readShort();
        var constTOffset = reader.readShort();
        var constUOffset = reader.readShort();
        var nextSize = reader.readShort();
        var jointWeightsOffset = reader.readShort();
        var numStreamedFrameSets = reader.readShort();
        var streamMethod = Md6AnimCompressionStreamMethod.fromValue(reader.readByte());
        reader.expectByte((byte) 0); // pad
        var assetScale = reader.readFloat();
        var startDelta = reader.readFloats(12);
        var endDelta = reader.readFloats(12);

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
