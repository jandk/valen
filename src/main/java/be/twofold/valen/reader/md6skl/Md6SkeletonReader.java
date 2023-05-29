package be.twofold.valen.reader.md6skl;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;

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
        List<Md6SkeletonJoint> bones = readJoints();
        return new Md6Skeleton(header, remapTable, bones);
    }

    private List<Md6SkeletonJoint> readJoints() {
        buffer.position(header.basePoseOffset() + 4);

        List<Vector4> rotations = buffer.getStructs(header.numJoints(), BetterBuffer::getVector4);
        buffer.skip(header.jointPadding() * 16);

        List<Vector3> scales = buffer.getStructs(header.numJoints(), BetterBuffer::getVector3);
        buffer.skip(header.jointPadding() * 12);

        List<Vector3> translations = buffer.getStructs(header.numJoints(), BetterBuffer::getVector3);
        buffer.skip(header.jointPadding() * 12);

        buffer.position(header.inverseBasePoseOffset() + 4);
        List<Mat4> inverseBasePoses = new ArrayList<>();
        for (int i = 0; i < header.numJoints(); i++) {
            float[] floats = new float[16];
            for (int j = 0; j < 12; j++) {
                floats[j] = buffer.getFloat();
            }
            floats[15] = 1;
            inverseBasePoses.add(Mat4.fromArray(floats));
        }
        buffer.skip(header.jointPadding() * 48);

        buffer.position(header.parentTblOffset() + 4);
        short[] parents = buffer.getShorts(header.numJoints());
        buffer.skip(header.jointPadding() * 2);

        buffer.position(header.size() + 4); // names are tacked on the end
        List<String> names = new ArrayList<>();
        for (int i = 0; i < header.numJoints(); i++) {
            names.add(buffer.getString());
        }

        return IntStream.range(0, header.numJoints())
            .mapToObj(i -> new Md6SkeletonJoint(
                names.get(i),
                parents[i],
                rotations.get(i),
                scales.get(i),
                translations.get(i)
            ))
            .toList();
    }

    private short[] readRemapTable() {
        buffer.position(header.skelRemapTblOffset() + 4);
        return buffer.getShorts(header.numJoints());
    }
}
