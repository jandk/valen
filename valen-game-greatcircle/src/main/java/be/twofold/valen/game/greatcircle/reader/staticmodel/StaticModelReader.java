package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.geometry.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.geometry.*;

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
    public Model read(BinaryReader reader, GreatCircleAsset asset) throws IOException {
        var model = StaticModel.read(reader, (Integer) asset.properties().get("version"));
        var meshes = new ArrayList<>(readMeshes(model, reader));

        if (readMaterials) {
            Materials.apply(archive, meshes, model.meshInfos(), StaticModelMeshInfo::mtlDecl, _ -> null);
        }
        return new Model(meshes, Optional.empty(), Optional.of(asset.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(StaticModel model, BinaryReader reader) throws IOException {
        if (!model.header().streamable()) {
            return readEmbeddedGeometry(model, reader);
        }
        return readStreamedGeometry(model, 0);
    }

    private List<Mesh> readEmbeddedGeometry(StaticModel model, BinaryReader reader) throws IOException {
        List<Mesh> meshes = new ArrayList<>();
        for (var meshInfo : model.meshInfos()) {
            Check.state(meshInfo.lodInfos().size() == 1);
            meshes.add(GeometryReader.readEmbeddedMesh(reader, meshInfo.lodInfos().getFirst()));
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
        var source = BinaryReader.fromBuffer(buffer);
        return GeometryReader.readStreamedMesh(source, lods, diskLayout.memoryLayouts(), false);
    }
}
