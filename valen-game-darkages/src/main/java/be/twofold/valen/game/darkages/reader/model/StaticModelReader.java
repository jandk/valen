package be.twofold.valen.game.darkages.reader.model;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.Hash;
import be.twofold.valen.game.darkages.reader.geometry.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class StaticModelReader implements AssetReader<Model, DarkAgesAsset> {
    private final DarkAgesArchive archive;
    private final boolean readMaterials;

    public StaticModelReader(DarkAgesArchive archive) {
        this(archive, true);
    }

    StaticModelReader(DarkAgesArchive archive, boolean readMaterials) {
        this.archive = archive;
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(DarkAgesAsset resource) {
        return resource.id().type() == ResourcesType.Model;
    }

    @Override
    public Model read(DataSource source, DarkAgesAsset resource) throws IOException {
        var model = StaticModel.read(source);
        var meshes = new ArrayList<>(readMeshes(model, source, resource.hash()));

        if (readMaterials) {
            Materials.apply(archive, meshes, model.meshInfos(), StaticModelMeshInfo::mtlDecl, _ -> null);
        }
        return new Model(meshes, Optional.empty(), Optional.of(resource.id().fullName()), Optional.empty(), Axis.Z);
    }

    private List<Mesh> readMeshes(StaticModel model, DataSource source, long hash) throws IOException {
        if (!model.header().streamable()) {
            return readEmbeddedGeometry(model, source);
        }
        return readStreamedGeometry(model, 0, hash);
    }

    private List<Mesh> readEmbeddedGeometry(StaticModel model, DataSource source) throws IOException {
        List<Mesh> meshes = new ArrayList<>();
        for (var meshInfo : model.meshInfos()) {
            Check.state(meshInfo.lodInfos().size() == 1);
            meshes.add(GeometryReader.readEmbeddedMesh(source, meshInfo.lodInfos().getFirst()));
        }
        return meshes;
    }

    private List<Mesh> readStreamedGeometry(StaticModel model, int lod, long hash) throws IOException {
        ByteBuffer key = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        key.putLong(hash);
        key.putInt(4 - lod);

        var streamHash = Hash.hash(key);
        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();

        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();

        var buffer = archive.readStream(streamHash, uncompressedSize);
        var source = DataSource.fromBuffer(buffer);
        return GeometryReader.readStreamedMesh(source, lods, false);
    }
}
