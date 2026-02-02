package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.geometry.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

public final class StaticModelReader implements AssetReader<Model, EternalAsset> {
    private final boolean readMaterials;

    public StaticModelReader() {
        this(true);
    }

    StaticModelReader(boolean readMaterials) {
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.Model;
    }

    @Override
    public Model read(BinarySource source, EternalAsset resource, LoadingContext context) throws IOException {
        var model = StaticModel.read(source);
        var meshes = new ArrayList<>(readMeshes(model, source, resource.hash(), context));

        if (readMaterials) {
            Materials.apply(context, meshes, model.meshInfos(), StaticModelMeshInfo::mtlDecl, _ -> null);
        }
        return new Model(meshes, Optional.empty(), Optional.of(resource.id().fullName()), Optional.empty(), Axis.Z);
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
        var streamHash = (hash << 4) | lod;
        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();

        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = model.streamDiskLayouts().get(lod).memoryLayouts();

        var bytes = context.open(new EternalStreamLocation(streamHash, uncompressedSize));
        var source = BinarySource.wrap(bytes);
        return GeometryReader.readStreamedMesh(source, lods, layouts, false);
    }

}
