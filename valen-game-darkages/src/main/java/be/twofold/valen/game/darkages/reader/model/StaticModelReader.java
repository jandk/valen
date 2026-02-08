package be.twofold.valen.game.darkages.reader.model;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.geometry.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

public final class StaticModelReader implements AssetReader<Model, DarkAgesAsset> {
    private final boolean readMaterials;

    public StaticModelReader(boolean readMaterials) {
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.Model;
    }

    @Override
    public Model read(BinarySource source, DarkAgesAsset asset, LoadingContext context) throws IOException {
        var model = StaticModel.read(source);
        var meshes = new ArrayList<>(readMeshes(model, source, asset.hash(), context));

        if (readMaterials) {
            Materials.apply(context, meshes, model.meshInfos(), StaticModelMeshInfo::mtlDecl, _ -> null);
        }
        return new Model(meshes, Optional.empty(), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(StaticModel model, BinarySource source, long hash, LoadingContext context) throws IOException {
        if (!model.header().streamable()) {
            return readEmbeddedGeometry(model, source);
        }
        return readStreamedGeometry(model, 0, hash, context);
    }

    private List<Mesh> readEmbeddedGeometry(StaticModel model, BinarySource source) throws IOException {
        List<Mesh> meshes = new ArrayList<>();
        for (var meshInfo : model.meshInfos()) {
            Check.state(meshInfo.lodInfos().size() == 1, "Expected single LOD");
            meshes.add(GeometryReader.readEmbeddedMesh(source, meshInfo.lodInfos().getFirst()));
        }
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(StaticModel model, int lod, long hash, LoadingContext context) throws IOException {
        var streamHash = Hash.hash(hash, 4 - lod, 0);
        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();

        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();

        var bytes = context.open(new DarkAgesStreamLocation(streamHash, uncompressedSize));
        var source = BinarySource.wrap(bytes);
        return GeometryReader.readStreamedMesh(source, lods, false);
    }
}
