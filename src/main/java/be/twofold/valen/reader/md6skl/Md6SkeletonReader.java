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
        List<Md6SkeletonBone> bones = readBones();

        System.out.println(bonesToDot(bones));

        return new Md6Skeleton(header, bones);
    }

    private String bonesToDot(List<Md6SkeletonBone> bones) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph G {\n");
        builder.append("  rankdir=LR;\n");
        for (int i = 0; i < bones.size(); i++) {
            Md6SkeletonBone bone = bones.get(i);
            builder.append("  ").append(i).append(" [label=\"").append(bone.name()).append("\"];\n");
            if (bone.parent() != -1) {
                builder.append("  ").append(bone.parent()).append(" -> ").append(i).append(";\n");
            }
        }
        builder.append("}\n");
        return builder.toString();
    }

    private List<Md6SkeletonBone> readBones() {
        buffer.position(header.offsets()[2] + 4); // This is weird...
        List<Vector4> quats = new ArrayList<>();
        for (int i = 0; i < header.boneCount(); i++) {
            quats.add(buffer.getVector4());
        }
        buffer.skip(header.emptyBones() * 16);

        List<Vector3> scale = new ArrayList<>();
        for (int i = 0; i < header.boneCount(); i++) {
            scale.add(buffer.getVector3());
        }
        buffer.skip(header.emptyBones() * 12);

        List<Vector3> position = new ArrayList<>();
        for (int i = 0; i < header.boneCount(); i++) {
            position.add(buffer.getVector3());
        }
        buffer.skip(header.emptyBones() * 12);

        buffer.position(header.offsets()[4] + 4); // This is weird...

        short[] parents = buffer.getShorts(header.boneCount());
        buffer.skip(header.emptyBones() * 2);

        short[] remap = buffer.getShorts(header.boneCount());
        buffer.skip(header.emptyBones() * 2);

        buffer.position(header.boneNamesOffset() + 4);
        List<String> names = new ArrayList<>();
        for (int i = 0; i < header.boneCount(); i++) {
            names.add(buffer.getString());
        }

        return IntStream.range(0, header.boneCount())
            .mapToObj(i -> new Md6SkeletonBone(
                names.get(i),
                parents[i],
                quats.get(i),
                position.get(i),
                scale.get(i)
            ))
            .toList();
    }
}
