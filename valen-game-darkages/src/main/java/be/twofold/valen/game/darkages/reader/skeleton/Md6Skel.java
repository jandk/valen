package be.twofold.valen.game.darkages.reader.skeleton;

import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.util.*;

public record Md6Skel(
    Md6SkelHeader header,
    short[] parentTable,
    short[] lastChildTable,
    short[] jointNameHandleTblOffset,
    short[] jointIndexTblOffset,
    short[] userChannelHandleTable,
    List<Quaternion> rotations,
    List<Vector3> scales,
    List<Vector3> translations,
    List<Matrix4> inverseBasePoses,
    short[] jointSetTable,
    short[] boundsJointTable,
    List<String> jointNames,
    List<String> userChannelNames
) {
    public static Md6Skel read(BinaryReader reader) throws IOException {
        var header = Md6SkelHeader.read(reader);
        var base = 4;

        reader.position(base + header.parentTblOffset());
        var parentTable = reader.readShorts(header.numJoints8());

        reader.position(base + header.lastChildTblOffset());
        var lastChildTable = reader.readShorts(header.numJoints8());

        reader.position(base + header.jointNameHandleTblOffset());
        var jointNameHandleTblOffset = reader.readShorts(header.numJointHandles8());

        reader.position(base + header.jointIndexTblOffset());
        var jointIndexTblOffset = reader.readShorts(header.numJoints8());

        reader.position(base + header.userChannelHandleTblOffset());
        var userChannelHandleTable = reader.readShorts(header.numUserChannels8());

        reader.position(base + header.basePoseOffset());
        var rotations = reader.readObjects(header.numJoints8(), Quaternion::read);
        var scales = reader.readObjects(header.numJoints8(), Vector3::read);
        var translations = reader.readObjects(header.numJoints8(), Vector3::read);

        reader.position(base + header.inverseBasePoseOffset());
        var inverseBasePoses = reader.readObjects(header.numJoints8(), Md6Skel::readInverseBasePose);

        reader.position(base + header.loadedDataSize() + header.jointSetTblOffset());
        var jointSetTable = reader.readShorts(Short.toUnsignedInt(reader.readShort()));

        reader.position(base + header.loadedDataSize() + header.boundsJointTblOffset());
        var boundsJointTable = reader.readShorts(Short.toUnsignedInt(reader.readShort()));

        reader.position(base + header.size());
        var jointNames = reader.readObjects(header.numJoints8(), BinaryReader::readPString);
        var userChannelNames = reader.readObjects(header.numUserChannels8(), BinaryReader::readPString);

        return new Md6Skel(
            header,
            parentTable,
            lastChildTable,
            jointNameHandleTblOffset,
            jointIndexTblOffset,
            userChannelHandleTable,
            rotations,
            scales,
            translations,
            inverseBasePoses,
            jointSetTable,
            boundsJointTable,
            jointNames,
            userChannelNames
        );
    }

    private static Matrix4 readInverseBasePose(BinaryReader reader) throws IOException {
        var floats = Arrays.copyOf(reader.readFloats(12), 16);
        floats[15] = 1.0f;
        return Matrix4.fromArray(floats).transpose();
    }
}
