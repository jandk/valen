package be.twofold.valen.game.greatcircle.reader.md6skl;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.io.*;
import java.util.stream.*;

public final class Md6SklReader implements AssetReader<Skeleton, GreatCircleAsset> {
    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.skeleton;
    }

    @Override
    public Skeleton read(BinaryReader reader, GreatCircleAsset asset) throws IOException {
        Md6Skl md6Skl1 = null;
        int skeleton1Length = reader.readInt();
        if (skeleton1Length != 0) {
            md6Skl1 = Md6Skl.read(reader);
        }

        Md6Skl md6Skl2 = null;
        int skeleton2Length = reader.readInt();
        if (skeleton2Length != 0) {
            md6Skl2 = Md6Skl.read(reader);
        }
        if (md6Skl1 == null) {
            throw new IOException("No skeleton found");
        }
        return map(md6Skl1);
    }

    public Skeleton map(Md6Skl md6Skl) {
        var bones = IntStream.range(0, md6Skl.header().numJoints())
            .mapToObj(i -> mapBone(md6Skl, i))
            .toList();

        return new Skeleton(bones, Axis.Z);
    }

    private Bone mapBone(Md6Skl md6Skl, int index) {
        return new Bone(
            md6Skl.jointNames().get(index),
            md6Skl.parentTbl().get(index),
            md6Skl.rotations().get(index),
            md6Skl.scales().get(index),
            md6Skl.translations().get(index),
            md6Skl.inverseBasePoses().get(index)
        );
    }
}
