package be.twofold.valen.game.darkages.reader.skeleton;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.resources.*;

import java.io.*;
import java.util.stream.*;

public final class Md6SkelReader implements AssetReader<Skeleton, DarkAgesAsset> {
    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.Skeleton;
    }

    @Override
    public Skeleton read(BinaryReader reader, DarkAgesAsset asset) throws IOException {
        int size = reader.readInt();
        if (size == 0) {
            throw new UnsupportedOperationException();
        }

        Md6Skel skeleton = Md6Skel.read(reader);
        return map(skeleton);
    }

    public Skeleton map(Md6Skel skeleton) {
        var bones = IntStream.range(0, skeleton.header().numJoints())
            .mapToObj(i -> mapBone(skeleton, i))
            .toList();

        return new Skeleton(bones, Axis.Z);
    }

    private Bone mapBone(Md6Skel skeleton, int index) {
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
