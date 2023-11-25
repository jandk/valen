package be.twofold.valen.reader.md6skl;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

import java.util.*;
import java.util.stream.*;

public final class Md6SkeletonReader {
    private final BetterBuffer buffer;

    private Md6SkeletonHeader header;

    public Md6SkeletonReader(BetterBuffer buffer) {
        this.buffer = buffer;
    }

    public Md6Skeleton read() {
        header = Md6SkeletonHeader.read(buffer);
        short[] remapTable = readRemapTable();
        List<Bone> bones = readJoints();
        return new Md6Skeleton(header, remapTable, bones);
    }

    private List<Bone> readJoints() {
        buffer.position(header.basePoseOffset() + 4);
        List<Quaternion> rotations = buffer.getStructs(header.numJoints8(), BetterBuffer::getQuaternion);
        List<Vector3> scales = buffer.getStructs(header.numJoints8(), BetterBuffer::getVector3);
        List<Vector3> translations = buffer.getStructs(header.numJoints8(), BetterBuffer::getVector3);

        buffer.position(header.inverseBasePoseOffset() + 4);
        List<Matrix4x4> inverseBasePoses = new ArrayList<>();
        for (int i = 0; i < header.numJoints8(); i++) {
            float[] floats = new float[16];
            for (int j = 0; j < 12; j++) {
                floats[j] = buffer.getFloat();
            }
            floats[15] = 1;
            inverseBasePoses.add(Matrix4x4.fromArray(floats));
        }

        buffer.position(header.parentTblOffset() + 4);
        short[] parents = buffer.getShorts(header.numJoints8());

        buffer.position(header.size() + 4); // names are tacked on the end
        List<String> names = new ArrayList<>();
        for (int i = 0; i < header.numJoints8(); i++) {
            names.add(buffer.getString());
        }

        return IntStream.range(0, header.numJoints())
            .mapToObj(i -> new Bone(
                names.get(i),
                parents[i],
                rotations.get(i),
                scales.get(i),
                translations.get(i),
                inverseBasePoses.get(i)
            ))
            .toList();
    }

    private short[] readRemapTable() {
        if (header.skelRemapTblOffset() == 0) {
            return new short[0];
        }
        buffer.position(header.skelRemapTblOffset() + 4);
        return buffer.getShorts(header.numJoints());
    }
}
