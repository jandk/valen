package be.twofold.valen.reader.md6skl;

import be.twofold.valen.core.geometry.*;

import java.util.stream.*;

public final class Md6SklMapper {
    private Md6SklMapper() {
    }

    public static Skeleton map(Md6Skl skeleton) {
        var bones = IntStream.range(0, skeleton.header().numJoints())
            .mapToObj(i -> mapBone(skeleton, i))
            .toList();

        return new Skeleton(bones);
    }

    private static Bone mapBone(Md6Skl skeleton, int index) {
        return new Bone(
            skeleton.names().get(index),
            skeleton.parents()[index],
            skeleton.rotations().get(index),
            skeleton.scales().get(index),
            skeleton.translations().get(index),
            skeleton.inverseBasePoses().get(index)
        );
    }
}
