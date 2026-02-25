package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.geometry.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class Md6ModelReader implements AssetReader<Model, EternalAsset> {
    private final boolean readMaterials;

    public Md6ModelReader(boolean readMaterials) {
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.BaseModel;
    }

    @Override
    public Model read(BinarySource source, EternalAsset asset, LoadingContext context) throws IOException {
        var model = Md6Model.read(source);
        var meshes = new ArrayList<>(readMeshes(model, asset.hash(), context));
        var skeletonKey = EternalAssetID.from(model.header().md6SkelName(), ResourceType.Skeleton);
        var skeleton = context.load(skeletonKey, Skeleton.class);

        if (readMaterials) {
            Materials.apply(context, meshes, model.meshInfos(), Md6ModelInfo::materialName, Md6ModelInfo::meshName);
        }
        return new Model(meshes, Optional.of(skeleton), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(Md6Model md6, long hash, LoadingContext context) throws IOException {
        var meshes = readStreamedGeometry(md6, 0, hash, context);
        fixJointIndices(md6, meshes);
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(Md6Model md6, int lod, long hash, LoadingContext context) throws IOException {
        var uncompressedSize = md6.layouts().get(lod).uncompressedSize();
        if (uncompressedSize == 0) {
            return List.of();
        }

        var lodInfos = md6.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = md6.layouts().get(lod).memoryLayouts();

        var identity = (hash << 4) | lod;
        var bytes = context.open(new EternalStreamLocation(identity, uncompressedSize));
        if (bytes.length() == 0) {
            throw new FileNotFoundException("Streamed geometry not found for hash: " + hash);
        }
        try (var source = BinarySource.wrap(bytes)) {
            return GeometryReader.readStreamedMesh(source, lodInfos, layouts, true);
        }
    }

    private void fixJointIndices(Md6Model md6, List<Mesh> meshes) {
        var jointRemap = md6.boneInfo().jointRemap();

        // This lookup table is in reverse... Nice
        var lookup = new byte[jointRemap.length()];
        for (var i = 0; i < jointRemap.length(); i++) {
            lookup[jointRemap.getUnsigned(i)] = (byte) i;
        }

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);

            // Just assume it's a mutable buffer, because we read it as such
            var joints = meshes.get(i).joints().map(Shorts.Mutable.class::cast).orElseThrow();
            for (var j = 0; j < joints.length(); j++) {
                joints.set(j, lookup[joints.getUnsigned(j) + meshInfo.unknown2()]);
            }
        }
    }
}
