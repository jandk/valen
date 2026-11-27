package be.twofold.valen.game.doom.readers.md6model;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Md6ModelReader implements AssetReader.Binary<Model, DoomAsset> {
    private final boolean readMaterials;

    public Md6ModelReader(boolean readMaterials) {
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(DoomAsset asset) {
        return asset.id().type() == ResourceType.of("baseModel");
    }

    @Override
    public Model read(BinaryReader reader, DoomAsset asset, LoadingContext context) throws IOException {
        reader.order(ByteOrder.BIG_ENDIAN);
        var model = Md6Model.read(reader);
        var skeletonKey = new DoomAssetID(model.header().skeleton(), ResourceType.of("skeleton"));
        var skeleton = context.load(skeletonKey, Skeleton.class);

        var meshes = model.meshes().stream()
            .map(mesh -> mesh.triangles().toMesh(true))
            .toList();

        if (readMaterials) {
            meshes = Materials.apply(context, meshes, model.meshes(), DoomAssetID::material, Md6Mesh::material, Md6Mesh::name);
        }

        fixJointIndices(model, meshes);

        return new Model(
            meshes,
            Optional.of(skeleton),
            Optional.of(asset.id().fullName()),
            Optional.empty(),
            Axis.Z
        );
    }

    private void fixJointIndices(Md6Model model, List<Mesh> meshes) {
        var jointRemap = model.header().jointRemap();

        // This lookup table is in reverse... Nice
        var lookup = new short[jointRemap.length()];
        for (short i = 0; i < jointRemap.length(); i++) {
            lookup[jointRemap.getUnsigned(i)] = i;
        }

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = model.meshes().get(i);

            // Just assume it's a mutable buffer, because we read it as such
            var joints = meshes.get(i).joints().map(Shorts.Mutable.class::cast).orElseThrow();
            for (var j = 0; j < joints.length(); j++) {
                joints.set(j, lookup[joints.getUnsigned(j) + meshInfo.jointOffset()]);
            }
        }
    }
}
