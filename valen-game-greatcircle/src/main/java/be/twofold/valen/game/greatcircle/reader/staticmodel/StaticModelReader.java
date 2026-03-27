package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.geometry.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

public final class StaticModelReader implements AssetReader.Binary<Model, GreatCircleAsset> {
    private final boolean readMaterials;

    public StaticModelReader(boolean readMaterials) {
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.model;
    }

    @Override
    public Model read(BinarySource source, GreatCircleAsset asset, LoadingContext context) throws IOException {
        var model = StaticModel.read(source, (Integer) asset.properties().get("version"));
        var meshes = new ArrayList<>(readMeshes(model, source, context));

        if (readMaterials) {
            Materials.apply(context, meshes, model.meshInfos(), StaticModelMeshInfo::mtlDecl, _ -> null);
        }
        return new Model(meshes, Optional.empty(), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(StaticModel model, BinarySource source, LoadingContext context) throws IOException {
        if (!model.header().streamable()) {
            return readEmbeddedGeometry(model, source);
        }
        return readStreamedGeometry(model, 0, context);
    }

    private List<Mesh> readEmbeddedGeometry(StaticModel model, BinarySource source) throws IOException {
        List<Mesh> meshes = new ArrayList<>();
        for (var meshInfo : model.meshInfos()) {
            Check.state(meshInfo.lodInfos().size() == 1, "Expected single LOD");
            meshes.add(GeometryReader.readEmbeddedMesh(source, meshInfo.lodInfos().getFirst()));
        }
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(StaticModel model, int lod, LoadingContext context) throws IOException {
        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();

        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();
        var bytes = context.open(new GreatCircleStreamLocation(diskLayout.hash(), uncompressedSize));
        var source = BinarySource.wrap(bytes);
        return GeometryReader.readStreamedMesh(source, lods, diskLayout.memoryLayouts(), false);
    }
}
