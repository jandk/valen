package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.geometry.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.util.*;

public final class StaticModelReader implements AssetReader<Model, EternalAsset> {
    private final EternalArchive archive;
    private final boolean readMaterials;

    public StaticModelReader(EternalArchive archive) {
        this(archive, true);
    }

    StaticModelReader(EternalArchive archive, boolean readMaterials) {
        this.archive = archive;
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.Model;
    }

    @Override
    public Model read(BinaryReader reader, EternalAsset resource) throws IOException {
        var model = StaticModel.read(reader);
        var meshes = new ArrayList<>(readMeshes(model, reader, resource.hash()));

        if (readMaterials) {
            Materials.apply(archive, meshes, model.meshInfos(), StaticModelMeshInfo::mtlDecl, _ -> null);
        }
        return new Model(meshes, Optional.empty(), Optional.of(resource.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(StaticModel model, BinaryReader reader, long hash) throws IOException {
        if (!model.header().streamable()) {
            return readEmbeddedGeometry(model, reader);
        }
        return readStreamedGeometry(model, 0, hash);
    }

    private List<Mesh> readEmbeddedGeometry(StaticModel model, BinaryReader reader) throws IOException {
        List<Mesh> meshes = new ArrayList<>();
        for (var meshInfo : model.meshInfos()) {
            Check.state(meshInfo.lodInfos().size() == 1);
            meshes.add(GeometryReader.readEmbeddedMesh(reader, meshInfo.lodInfos().getFirst()));
        }
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(StaticModel model, int lod, long hash) throws IOException {
        var streamHash = (hash << 4) | lod;
        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();

        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = model.streamDiskLayouts().get(lod).memoryLayouts();

        var buffer = archive.readStream(streamHash, uncompressedSize);
        var source = BinaryReader.fromBuffer(buffer);
        return GeometryReader.readStreamedMesh(source, lods, layouts, false);
    }

}
