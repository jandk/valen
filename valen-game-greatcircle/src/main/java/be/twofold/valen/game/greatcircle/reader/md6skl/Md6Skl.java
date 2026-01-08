package be.twofold.valen.game.greatcircle.reader.md6skl;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.util.*;

public record Md6Skl(
    Md6SklHeader header,
    Bytes animationMask,
    Shorts parentTbl,
    Shorts lastChildTbl,
    Shorts jointHandleTbl,
    Shorts userChannelHandleTbl,
    Shorts rigControlHandleTbl,
    List<Quaternion> rotations,
    List<Vector3> scales,
    List<Vector3> translations,
    List<Matrix4> inverseBasePoses,
    Ints unknownInts,
    List<String> jointNames,
    List<String> userChannelNames,
    List<String> rigControlNames
) {
    public static Md6Skl read(BinarySource source) throws IOException {
        var base = source.position();

        var header = Md6SklHeader.read(source);
        var numJoints8 = (header.numJoints() + 7) & ~7;
        var numUserChannels8 = (header.numUserChannels() + 7) & ~7;
        var numRigControls8 = (header.numRigControls() + 7) & ~7;

        source.position(base + header.animationMaskOffset());
        var animationMask = source.readBytes(numJoints8);

        source.position(base + header.parentTblOffset());
        var parentTbl = source.readShorts(numJoints8);

        source.position(base + header.lastChildTblOffset());
        var lastChildTbl = source.readShorts(numJoints8);

        source.position(base + header.jointHandleTblOffset());
        var jointHandleTbl = source.readShorts(numJoints8);

        source.position(base + header.userChannelHandleTblOffset());
        var userChannelHandleTbl = source.readShorts(numUserChannels8);

        source.position(base + header.rigControlHandleTblOffset());
        var rigControlHandleTbl = source.readShorts(numRigControls8);

        source.position(base + header.basePoseOffset());
        var rotations = source.readObjects(numJoints8, Quaternion::read);
        var scales = source.readObjects(numJoints8, Vector3::read);
        var translations = source.readObjects(numJoints8, Vector3::read);

        source.position(base + header.inverseBasePoseOffset());
        var inverseBasePoses = source.readObjects(numJoints8, Matrix4::read3x4);

        source.position(base + header.size());
        var unknownInts = source.readInts(numUserChannels8);
        var jointNames = source.readStrings(numJoints8, StringFormat.INT_LENGTH);
        var userChannelNames = source.readStrings(numUserChannels8, StringFormat.INT_LENGTH);
        var rigControlNames = source.readStrings(numRigControls8, StringFormat.INT_LENGTH);

        return new Md6Skl(
            header,
            animationMask,
            parentTbl,
            lastChildTbl,
            jointHandleTbl,
            userChannelHandleTbl,
            rigControlHandleTbl,
            rotations,
            scales,
            translations,
            inverseBasePoses,
            unknownInts,
            jointNames,
            userChannelNames,
            rigControlNames
        );
    }
}
