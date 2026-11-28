package be.twofold.valen.game.doom.readers.md6skel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.doom.readers.common.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

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
            skeleton.translations().get(index).multiply(Triangles.UNIT_SCALE),
            scaleTranslation(skeleton.inverseBasePoses().get(index), Triangles.UNIT_SCALE)
        );
    }

    private static Matrix4 scaleTranslation(Matrix4 m, float scale) {
        return new Matrix4(
            m.m11(), m.m21(), m.m31(), m.m41(),
            m.m12(), m.m22(), m.m32(), m.m42(),
            m.m13(), m.m23(), m.m33(), m.m43(),
            m.m14() * scale, m.m24() * scale, m.m34() * scale, m.m44()
        );
    }
}
