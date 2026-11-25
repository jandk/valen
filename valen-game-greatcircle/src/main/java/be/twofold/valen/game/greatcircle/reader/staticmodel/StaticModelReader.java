package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;

public final class StaticModelReader implements AssetReader.Binary<Model, GreatCircleAsset> {
    private final boolean readMaterials;

    public StaticModelReader(boolean readMaterials) {
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.of("model");
    }

    @Override
    public Model read(BinaryReader reader, GreatCircleAsset asset, LoadingContext context) throws IOException {
        var model = StaticModel.read(reader, asset.version());
        var meshes = readMeshes(model, reader, context);

        if (readMaterials) {
            meshes = Materials.apply(context, meshes, model.meshInfos(), GreatCircleAssetID::material, StaticModelMeshInfo::mtlDecl, _ -> null);
        }
        return new Model(meshes, Optional.empty(), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    @Override
    public Optional<Meta.Node> readMetadata(GreatCircleAsset asset, LoadingContext context) throws IOException {
        try (var reader = BinaryReader.wrap(context.open(asset.location()))) {
            var model = StaticModel.read(reader, asset.version());
            return Optional.of(Meta.build(MethodHandles.lookup(), model));
        }
    }

    private List<Mesh> readMeshes(StaticModel model, BinaryReader reader, LoadingContext context) throws IOException {
        if (!model.header().streamable()) {
            return readEmbeddedGeometry(model, reader);
        }
        return readStreamedGeometry(model, 0, context);
    }

    private List<Mesh> readEmbeddedGeometry(StaticModel model, BinaryReader reader) throws IOException {
        List<Mesh> meshes = new ArrayList<>();
        for (var meshInfo : model.meshInfos()) {
            Check.state(meshInfo.lodInfos().size() == 1, "Expected single LOD");
            meshes.add(GeometryReader.readEmbeddedMesh(reader, meshInfo.lodInfos().getFirst()));
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
        var reader = BinaryReader.wrap(bytes);
        return GeometryReader.readStreamedMesh(reader, lods, diskLayout.memoryLayouts(), false);
    }
}
