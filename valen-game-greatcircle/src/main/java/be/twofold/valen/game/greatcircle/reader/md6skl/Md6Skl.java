package be.twofold.valen.game.greatcircle.reader.md6skl;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;

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
        var inverseBasePoses = reader.readObjects(numJoints8, Md6Skl::readInverseBasePose);

        reader.position(base + header.size());
        var unknownInts = reader.readInts(numUserChannels8);
        var jointNames = reader.readObjects(numJoints8, BinaryReader::readPString);
        var userChannelNames = reader.readObjects(numUserChannels8, BinaryReader::readPString);
        var rigControlNames = reader.readObjects(numRigControls8, BinaryReader::readPString);

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

    private static Matrix4 readInverseBasePose(BinaryReader reader) throws IOException {
        var floats = Floats.Mutable.allocate(16);
        reader.readFloats(12).copyTo(floats, 0);
        floats.set(15, 1.0f);
        return Matrix4.fromFloats(floats).transpose();
    }
}
