package be.twofold.valen.game.eternal.reader.md6skel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;
import java.util.stream.*;

public final class Md6SkelReader implements AssetReader.Binary<Skeleton, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.Skeleton;
    }

    @Override
    public Skeleton read(BinarySource source, EternalAsset asset, LoadingContext context) throws IOException {
        Md6Skel skeleton = Md6Skel.read(source);
        return map(skeleton);
    }

    @Override
    public Optional<Meta.Node> readMetadata(EternalAsset asset, LoadingContext context) throws IOException {
        try (var source = BinarySource.wrap(context.open(asset.location()))) {
            var skeleton = Md6Skel.read(source);
            return Optional.of(Meta.build(MethodHandles.lookup(), skeleton));
        }
    }

    public Skeleton map(Md6Skel skeleton) {
        var bones = IntStream.range(0, skeleton.header().numJoints())
            .mapToObj(i -> mapBone(skeleton, i))
            .toList();

        return new Skeleton(bones, Axis.Z);
    }

    private Bone mapBone(Md6Skel skeleton, int index) {
        return new Bone(
            skeleton.names().get(index),
            skeleton.parents().get(index),
            skeleton.rotations().get(index),
            skeleton.scales().get(index),
            skeleton.translations().get(index),
            skeleton.inverseBasePoses().get(index)
        );
    }
}
