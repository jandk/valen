package be.twofold.valen.game.greatcircle.reader.md6skl;

import be.twofold.valen.core.io.BinaryReader;
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
    public static Md6Skl read(BinaryReader reader) throws IOException {
        long base = reader.position();

        Md6SklHeader header = Md6SklHeader.read(reader);
        int numJoints8 = (header.numJoints() + 7) & ~7;
        int numUserChannels8 = (header.numUserChannels() + 7) & ~7;
        int numRigControls8 = (header.numRigControls() + 7) & ~7;

        reader.position(base + header.animationMaskOffset());
        byte[] animationMask = reader.readBytes(numJoints8);

        reader.position(base + header.parentTblOffset());
        short[] parentTbl = reader.readShorts(numJoints8);

        reader.position(base + header.lastChildTblOffset());
        short[] lastChildTbl = reader.readShorts(numJoints8);

        reader.position(base + header.jointHandleTblOffset());
        short[] jointHandleTbl = reader.readShorts(numJoints8);

        reader.position(base + header.userChannelHandleTblOffset());
        short[] userChannelHandleTbl = reader.readShorts(numUserChannels8);

        reader.position(base + header.rigControlHandleTblOffset());
        short[] rigControlHandleTbl = reader.readShorts(numRigControls8);

        reader.position(base + header.basePoseOffset());
        List<Quaternion> rotations = reader.readObjects(numJoints8, Quaternion::read);
        List<Vector3> scales = reader.readObjects(numJoints8, Vector3::read);
        List<Vector3> translations = reader.readObjects(numJoints8, Vector3::read);

        reader.position(base + header.inverseBasePoseOffset());
        List<Matrix4> inverseBasePoses = reader.readObjects(numJoints8, Md6Skl::readInverseBasePose);

        reader.position(base + header.size());
        int[] unknownInts = reader.readInts(numUserChannels8);
        List<String> jointNames = reader.readObjects(numJoints8, BinaryReader::readPString);
        List<String> userChannelNames = reader.readObjects(numUserChannels8, BinaryReader::readPString);
        List<String> rigControlNames = reader.readObjects(numRigControls8, BinaryReader::readPString);

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
        float[] floats = Arrays.copyOf(reader.readFloats(12), 16);
        floats[15] = 1.0f;
        return Matrix4.fromArray(floats).transpose();
    }
}
