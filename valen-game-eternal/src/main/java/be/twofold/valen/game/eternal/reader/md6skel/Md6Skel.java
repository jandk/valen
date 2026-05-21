package be.twofold.valen.game.eternal.reader.md6skel;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

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
    public static Md6Skel read(BinarySource source) throws IOException {
        var header = Md6SkelHeader.read(source);

        // source.expectPosition(header.basePoseOffset() + 4);
        var rotations = source.readObjects(header.numJoints8(), Quaternion::read);
        var scales = source.readObjects(header.numJoints8(), Vector3::read);
        var translations = source.readObjects(header.numJoints8(), Vector3::read);

        source.position(header.parentTblOffset() + 4);
        var parents = source.readShorts(header.numJoints8());

        source.position(header.inverseBasePoseOffset() + 4);
        var inverseBasePoses = source.readObjects(header.numJoints8(), Matrix4::read3x4);

        // source.expectPosition(header.size() + 4); // names are tacked on the end
        var names = source.readStrings(header.numJoints8(), StringFormat.INT_LENGTH);

        return new Md6Skel(header, rotations, scales, translations, parents, inverseBasePoses, names);
    }
}
