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
    public static Md6Skl read(BinaryReader reader) throws IOException {
        var base = reader.position();

        var header = Md6SklHeader.read(reader);
        var numJoints8 = (header.numJoints() + 7) & ~7;
        var numUserChannels8 = (header.numUserChannels() + 7) & ~7;
        var numRigControls8 = (header.numRigControls() + 7) & ~7;

        reader.position(base + header.animationMaskOffset());
        var animationMask = reader.readBytes(numJoints8);

        reader.position(base + header.parentTblOffset());
        var parentTbl = reader.readShorts(numJoints8);

        reader.position(base + header.lastChildTblOffset());
        var lastChildTbl = reader.readShorts(numJoints8);

        reader.position(base + header.jointHandleTblOffset());
        var jointHandleTbl = reader.readShorts(numJoints8);

        reader.position(base + header.userChannelHandleTblOffset());
        var userChannelHandleTbl = reader.readShorts(numUserChannels8);

        reader.position(base + header.rigControlHandleTblOffset());
        var rigControlHandleTbl = reader.readShorts(numRigControls8);

        reader.position(base + header.basePoseOffset());
        var rotations = reader.readObjects(numJoints8, Quaternion::read);
        var scales = reader.readObjects(numJoints8, Vector3::read);
        var translations = reader.readObjects(numJoints8, Vector3::read);

        reader.position(base + header.inverseBasePoseOffset());
        var inverseBasePoses = reader.readObjects(numJoints8, Matrix4::read3x4);

        reader.position(base + header.size());
        var unknownInts = reader.readInts(numUserChannels8);
        var jointNames = reader.readStrings(numJoints8, StringFormat.INT_LENGTH);
        var userChannelNames = reader.readStrings(numUserChannels8, StringFormat.INT_LENGTH);
        var rigControlNames = reader.readStrings(numRigControls8, StringFormat.INT_LENGTH);

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
