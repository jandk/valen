package be.twofold.valen.game.greatcircle.reader.md6skl;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.util.*;

public record Md6Skl(
    Md6SklHeader header,
    byte[] animationMask,
    short[] parentTbl,
    short[] lastChildTbl,
    short[] jointHandleTbl,
    short[] userChannelHandleTbl,
    short[] rigControlHandleTbl,
    List<Quaternion> rotations,
    List<Vector3> scales,
    List<Vector3> translations,
    List<Matrix4> inverseBasePoses,
    int[] unknownInts,
    List<String> jointNames,
    List<String> userChannelNames,
    List<String> rigControlNames
) {
    public static Md6Skl read(DataSource source) throws IOException {
        long base = source.position();

        Md6SklHeader header = Md6SklHeader.read(source);
        int numJoints8 = (header.numJoints() + 7) & ~7;
        int numUserChannels8 = (header.numUserChannels() + 7) & ~7;
        int numRigControls8 = (header.numRigControls() + 7) & ~7;

        source.position(base + header.animationMaskOffset());
        byte[] animationMask = source.readBytes(numJoints8);

        source.position(base + header.parentTblOffset());
        short[] parentTbl = source.readShorts(numJoints8);

        source.position(base + header.lastChildTblOffset());
        short[] lastChildTbl = source.readShorts(numJoints8);

        source.position(base + header.jointHandleTblOffset());
        short[] jointHandleTbl = source.readShorts(numJoints8);

        source.position(base + header.userChannelHandleTblOffset());
        short[] userChannelHandleTbl = source.readShorts(numUserChannels8);

        source.position(base + header.rigControlHandleTblOffset());
        short[] rigControlHandleTbl = source.readShorts(numRigControls8);

        source.position(base + header.basePoseOffset());
        List<Quaternion> rotations = source.readObjects(numJoints8, Quaternion::read);
        List<Vector3> scales = source.readObjects(numJoints8, Vector3::read);
        List<Vector3> translations = source.readObjects(numJoints8, Vector3::read);

        source.position(base + header.inverseBasePoseOffset());
        List<Matrix4> inverseBasePoses = source.readObjects(numJoints8, Md6Skl::readInverseBasePose);

        source.position(base + header.size());
        int[] unknownInts = source.readInts(numUserChannels8);
        List<String> jointNames = source.readObjects(numJoints8, DataSource::readPString);
        List<String> userChannelNames = source.readObjects(numUserChannels8, DataSource::readPString);
        List<String> rigControlNames = source.readObjects(numRigControls8, DataSource::readPString);

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

    private static Matrix4 readInverseBasePose(DataSource source) throws IOException {
        float[] floats = Arrays.copyOf(source.readFloats(12), 16);
        floats[15] = 1.0f;
        return Matrix4.fromArray(floats).transpose();
    }
}
