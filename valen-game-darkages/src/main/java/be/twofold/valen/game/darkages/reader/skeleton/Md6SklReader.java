package be.twofold.valen.game.darkages.reader.skeleton;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.resources.*;

import java.io.*;
import java.util.stream.*;

public final class Md6SklReader implements AssetReader<Skeleton, DarkAgesAsset> {
    @Override
    public boolean canRead(DarkAgesAsset resource) {
        return resource.id().type() == ResourcesType.Skeleton;
    }

    @Override
    public Skeleton read(DataSource source, DarkAgesAsset resource) throws IOException {
        int size = source.readInt();
        if (size == 0) {
            throw new UnsupportedOperationException();
        }

        Md6Skl skeleton = Md6Skl.read(source);
        return map(skeleton);
    }

    public Skeleton map(Md6Skl skeleton) {
        var bones = IntStream.range(0, skeleton.header().numJoints())
            .mapToObj(i -> mapBone(skeleton, i))
            .toList();

        return new Skeleton(bones, Axis.Z);
    }

    private Bone mapBone(Md6Skl skeleton, int index) {
        return new Bone(
            skeleton.jointNames().get(index),
            skeleton.parentTable()[index],
            skeleton.rotations().get(index),
            skeleton.scales().get(index),
            skeleton.translations().get(index),
            skeleton.inverseBasePoses().get(index)
        );
    }
}
