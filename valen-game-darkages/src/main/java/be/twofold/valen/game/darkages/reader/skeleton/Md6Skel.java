package be.twofold.valen.game.darkages.reader.skeleton;

import be.twofold.valen.core.math.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Md6Skel(
    Md6SkelHeader header,
    Shorts parentTable,
    Shorts lastChildTable,
    Shorts jointNameHandleTblOffset,
    Shorts jointIndexTblOffset,
    Shorts userChannelHandleTable,
    List<Quaternion> rotations,
    List<Vector3> scales,
    List<Vector3> translations,
    List<Matrix4> inverseBasePoses,
    Shorts jointSetTable,
    Shorts boundsJointTable,
    List<String> jointNames,
    List<String> userChannelNames
) {
    public static Md6Skel read(BinarySource source) throws IOException {
        var header = Md6SkelHeader.read(source);
        var base = 4;

        source.position(base + header.parentTblOffset());
        var parentTable = source.readShorts(header.numJoints8());

        source.position(base + header.lastChildTblOffset());
        var lastChildTable = source.readShorts(header.numJoints8());

        source.position(base + header.jointNameHandleTblOffset());
        var jointNameHandleTblOffset = source.readShorts(header.numJointHandles8());

        source.position(base + header.jointIndexTblOffset());
        var jointIndexTblOffset = source.readShorts(header.numJoints8());

        source.position(base + header.userChannelHandleTblOffset());
        var userChannelHandleTable = source.readShorts(header.numUserChannels8());

        source.position(base + header.basePoseOffset());
        var rotations = source.readObjects(header.numJoints8(), Quaternion::read);
        var scales = source.readObjects(header.numJoints8(), Vector3::read);
        var translations = source.readObjects(header.numJoints8(), Vector3::read);

        source.position(base + header.inverseBasePoseOffset());
        var inverseBasePoses = source.readObjects(header.numJoints8(), Md6Skel::readInverseBasePose);

        source.position(base + header.loadedDataSize() + header.jointSetTblOffset());
        var jointSetTable = source.readShorts(source.readShort());

        source.position(base + header.loadedDataSize() + header.boundsJointTblOffset());
        var boundsJointTable = source.readShorts(source.readShort());

        source.position(base + header.size());
        var jointNames = source.readStrings(header.numJoints8(), StringFormat.INT_LENGTH);
        var userChannelNames = source.readStrings(header.numUserChannels8(), StringFormat.INT_LENGTH);

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

    private static Matrix4 readInverseBasePose(BinarySource source) throws IOException {
        var floats = Floats.Mutable.allocate(16);
        source.readFloats(12).copyTo(floats, 0);
        floats.set(15, 1.0f);
        return Matrix4.fromFloats(floats).transpose();
    }
}
