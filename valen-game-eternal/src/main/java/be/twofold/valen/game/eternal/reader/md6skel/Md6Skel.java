package be.twofold.valen.game.eternal.reader.md6skel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;

public record Md6Skel(
    Md6SkelHeader header,
    List<Quaternion> rotations,
    List<Vector3> scales,
    List<Vector3> translations,
    Shorts parents,
    List<Matrix4> inverseBasePoses,
    List<String> names
) {
    public static Md6Skel read(BinaryReader reader) throws IOException {
        var header = Md6SkelHeader.read(reader);

        reader.expectPosition(header.basePoseOffset() + 4);
        var rotations = reader.readObjects(header.numJoints8(), Quaternion::read);
        var scales = reader.readObjects(header.numJoints8(), Vector3::read);
        var translations = reader.readObjects(header.numJoints8(), Vector3::read);

        reader.position(header.parentTblOffset() + 4);
        var parents = reader.readShorts(header.numJoints8());

        reader.position(header.inverseBasePoseOffset() + 4);
        var inverseBasePoses = reader.readObjects(header.numJoints8(), Md6Skel::readInverseBasePose);

        reader.expectPosition(header.size() + 4); // names are tacked on the end
        var names = reader.readObjects(header.numJoints8(), BinaryReader::readPString);

        return new Md6Skel(header, rotations, scales, translations, parents, inverseBasePoses, names);
    }

    private static Matrix4 readInverseBasePose(BinaryReader reader) throws IOException {
        var floats = MutableFloats.allocate(16);
        reader.readFloats(12).copyTo(floats, 0);
        floats.set(15, 1.0f);
        return Matrix4.fromFloats(floats).transpose();
    }
}
