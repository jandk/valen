package be.twofold.valen.reader.md6skel;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

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
    public static Md6Skel read(BetterBuffer buffer) {
        var header = Md6SkelHeader.read(buffer);

        buffer.expectPosition(header.basePoseOffset() + 4);
        var rotations = buffer.getStructs(header.numJoints8(), Quaternion::read);
        var scales = buffer.getStructs(header.numJoints8(), Vector3::read);
        var translations = buffer.getStructs(header.numJoints8(), Vector3::read);

        buffer.position(header.parentTblOffset() + 4);
        var parents = buffer.getShorts(header.numJoints8());

        buffer.position(header.inverseBasePoseOffset() + 4);
        var inverseBasePoses = readInverseBasePoses(buffer, header);

        buffer.expectPosition(header.size() + 4); // names are tacked on the end
        var names = buffer.getStructs(header.numJoints8(), BetterBuffer::getString);

        return new Md6Skel(header, rotations, scales, translations, parents, inverseBasePoses, names);
    }

    private static List<Matrix4> readInverseBasePoses(BetterBuffer buffer, Md6SkelHeader header) {
        List<Matrix4> inverseBasePoses = new ArrayList<>();
        for (var i = 0; i < header.numJoints8(); i++) {
            var floats = new float[16];
            for (var j = 0; j < 12; j++) {
                floats[j] = buffer.getFloat();
            }
            floats[15] = 1;
            inverseBasePoses.add(Matrix4.fromArray(floats).transpose());
        }
        return List.copyOf(inverseBasePoses);
    }
}
