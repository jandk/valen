package be.twofold.valen.game.eternal.reader.md6skel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.reader.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.util.stream.*;

public final class Md6SkelReader implements ResourceReader<Skeleton> {
    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.Skeleton;
    }

    @Override
    public Skeleton read(DataSource source, Asset asset) throws IOException {
        Md6Skel skeleton = Md6Skel.read(source);
        return map(skeleton);
    }

    public Skeleton map(Md6Skel skeleton) {
        var bones = IntStream.range(0, skeleton.header().numJoints())
            .mapToObj(i -> mapBone(skeleton, i))
            .toList();

        return new Skeleton(bones);
    }

    private Bone mapBone(Md6Skel skeleton, int index) {
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
