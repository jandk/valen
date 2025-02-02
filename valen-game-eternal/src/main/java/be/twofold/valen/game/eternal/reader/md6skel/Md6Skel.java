package be.twofold.valen.game.eternal.reader.md6skel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.util.*;

public record Md6Skel(
    Md6SkelHeader header,
    List<Quaternion> rotations,
    List<Vector3> scales,
    List<Vector3> translations,
    short[] parents,
    List<Matrix4> inverseBasePoses,
    List<String> names
) {
    public static Md6Skel read(DataSource source) throws IOException {
        var header = Md6SkelHeader.read(source);

        source.expectPosition(header.basePoseOffset() + 4);
        var rotations = source.readObjects(header.numJoints8(), Quaternion::read);
        var scales = source.readObjects(header.numJoints8(), Vector3::read);
        var translations = source.readObjects(header.numJoints8(), Vector3::read);

        source.position(header.parentTblOffset() + 4);
        var parents = source.readShorts(header.numJoints8());

        source.position(header.inverseBasePoseOffset() + 4);
        var inverseBasePoses = readInverseBasePoses(source, header);

        source.expectPosition(header.size() + 4); // names are tacked on the end
        var names = source.readObjects(header.numJoints8(), DataSource::readPString);

        return new Md6Skel(header, rotations, scales, translations, parents, inverseBasePoses, names);
    }

    private static List<Matrix4> readInverseBasePoses(DataSource source, Md6SkelHeader header) throws IOException {
        List<Matrix4> inverseBasePoses = new ArrayList<>();
        for (var i = 0; i < header.numJoints8(); i++) {
            var floats = Arrays.copyOf(source.readFloats(12), 16);
            floats[15] = 1;
            Matrix4 matrix4 = Matrix4.fromArray(floats).transpose();
            inverseBasePoses.add(matrix4);
        }
        return List.copyOf(inverseBasePoses);
    }
}
