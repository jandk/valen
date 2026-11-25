package be.twofold.valen.game.doom.readers.md6skel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;
import java.util.stream.*;

public final class Md6SkelReader implements AssetReader.Binary<Skeleton, DoomAsset> {
    @Override
    public boolean canRead(DoomAsset asset) {
        return asset.id().type() == ResourceType.of("skeleton");
    }

    @Override
    public Skeleton read(BinaryReader reader, DoomAsset asset, LoadingContext context) throws IOException {
        var md6Skel = Md6Skel.read(reader);
        return map(md6Skel);
    }

    @Override
    public Optional<Meta.Node> readMetadata(DoomAsset asset, LoadingContext context) throws IOException {
        try (var reader = BinaryReader.wrap(context.open(asset.location()))) {
            var skeleton = Md6Skel.read(reader);
            return Optional.of(Meta.build(MethodHandles.lookup(), skeleton));
        }
    }

    public Skeleton map(Md6Skel skeleton) {
        var bones = IntStream.range(0, skeleton.data().numJoints())
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
