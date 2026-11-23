package be.twofold.valen.game.darkages.reader.skeleton;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

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
        var inverseBasePoses = reader.readObjects(header.numJoints8(), Matrix4::read3x4);

        reader.position(base + header.loadedDataSize() + header.jointSetTblOffset());
        var jointSetTable = reader.readShorts(reader.readShort());

        reader.position(base + header.loadedDataSize() + header.boundsJointTblOffset());
        var boundsJointTable = reader.readShorts(reader.readShort());

        reader.position(base + header.size());
        var jointNames = reader.readStrings(header.numJoints8(), StringFormat.INT_LENGTH);
        var userChannelNames = reader.readStrings(header.numUserChannels8(), StringFormat.INT_LENGTH);

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
}
