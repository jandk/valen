package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.geometry.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.io.*;
import java.util.*;

public final class StaticModelReader implements AssetReader<Model, GreatCircleAsset> {
    private final GreatCircleArchive archive;
    private final boolean readMaterials;

    public StaticModelReader(GreatCircleArchive archive) {
        this(archive, true);
    }

    StaticModelReader(GreatCircleArchive archive, boolean readMaterials) {
        this.archive = archive;
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.model;
    }

    @Override
    public Model read(DataSource source, GreatCircleAsset asset) throws IOException {
        var model = StaticModel.read(source, (Integer) asset.properties().get("version"));
        var meshes = new ArrayList<>(readMeshes(model, source));

        if (readMaterials) {
            Materials.apply(archive, meshes, model.meshInfos(), StaticModelMeshInfo::mtlDecl, _ -> null);
        }
        return new Model(meshes, Optional.empty(), Optional.of(asset.id().fullName()), Axis.Z);
    }

    private List<Mesh> readMeshes(StaticModel model, DataSource source) throws IOException {
        if (!model.header().streamable()) {
            return readEmbeddedGeometry(model, source);
        }
        return readStreamedGeometry(model, 0);
    }

    private List<Mesh> readEmbeddedGeometry(StaticModel model, DataSource source) throws IOException {
        List<Mesh> meshes = new ArrayList<>();
        for (var meshInfo : model.meshInfos()) {
            Check.state(meshInfo.lodInfos().size() == 1);
            meshes.add(GeometryReader.readEmbeddedMesh(source, meshInfo.lodInfos().getFirst()));
        }
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(StaticModel model, int lod) throws IOException {
        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();

        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();
        var buffer = archive.readStream(diskLayout.hash(), uncompressedSize);
        var source = DataSource.fromBuffer(buffer);
        return GeometryReader.readStreamedMesh(source, lods, diskLayout.memoryLayouts(), false);
    }
}
