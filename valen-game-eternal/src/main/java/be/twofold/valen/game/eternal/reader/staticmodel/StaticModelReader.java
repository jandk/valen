package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.geometry.*;
import be.twofold.valen.game.eternal.resource.*;

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
    public Model read(DataSource source, EternalAsset resource) throws IOException {
        var model = StaticModel.read(source);
        var meshes = new ArrayList<>(readMeshes(model, source, resource.hash()));

        if (readMaterials) {
            var materials = new HashMap<String, Material>();
            for (int i = 0; i < meshes.size(); i++) {
                var meshInfo = model.meshInfos().get(i);
                var materialName = meshInfo.mtlDecl();
                var materialFile = "generated/decls/material2/" + materialName + ".decl";
                if (!materials.containsKey(materialName)) {
                    var assetId = EternalAssetID.from(materialFile, ResourceType.RsStreamFile);
                    var material = archive.loadAsset(assetId, Material.class);
                    materials.put(materialName, material);
                }
                meshes.set(i, meshes.get(i)
                    .withMaterial(Optional.of(materials.get(materialName))));
            }
        }
        return new Model(meshes, Optional.empty(), Optional.of(resource.id().fullName()), Axis.Z);
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
        var streamHash = (hash << 4) | lod;
        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();

        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = model.streamDiskLayouts().get(lod).memoryLayouts();

        var buffer = archive.readStream(streamHash, uncompressedSize);
        var source = DataSource.fromBuffer(buffer);
        return GeometryReader.readStreamedMesh(source, lods, layouts, false);
    }

}
