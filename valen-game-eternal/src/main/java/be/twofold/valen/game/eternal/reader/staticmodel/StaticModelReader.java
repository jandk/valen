package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.*;
import be.twofold.valen.game.eternal.reader.geometry.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.util.*;

public final class StaticModelReader implements ResourceReader<Model> {
    private final EternalArchive archive;
    private final boolean readStreams;
    private final boolean readMaterials;

    public StaticModelReader(EternalArchive archive) {
        this(archive, true, true);
    }

    StaticModelReader(
        EternalArchive archive,
        boolean readStreams,
        boolean readMaterials
    ) {
        this.archive = archive;
        this.readStreams = readStreams;
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.Model;
    }

    @Override
    public Model read(DataSource source, Asset asset) throws IOException {
        var model = read(source, (Long) asset.properties().get("hash"));
        return new Model(List.of(new SubModel(model.meshes())), model.materials(), null);
    }

    public StaticModel read(DataSource source, long hash) throws IOException {
        var model = StaticModel.read(source);

        model = model.withMeshes(readMeshes(model, source, hash));

        if (readMaterials) {
            var materials = new LinkedHashMap<String, Material>();
            var materialIndices = new HashMap<String, Integer>();

            var meshes = new ArrayList<Mesh>();
            for (int i = 0; i < model.meshes().size(); i++) {
                var meshInfo = model.meshInfos().get(i);
                var materialName = meshInfo.mtlDecl();
                var materialFile = "generated/decls/material2/" + materialName + ".decl";
                var materialIndex = materialIndices.computeIfAbsent(materialName, k -> materials.size());
                if (!materials.containsKey(materialName)) {
                    var assetId = ResourceKey.from(materialFile, ResourceType.RsStreamFile);
                    var material = (Material) archive.loadAsset(assetId);
                    materials.put(materialName, material);
                }
                meshes.add(model.meshes().get(i).withMaterialIndex(materialIndex));
            }
            model = model
                .withMeshes(meshes)
                .withMaterials(List.copyOf(materials.values()));
        }
        return model;
    }

    private List<Mesh> readMeshes(StaticModel model, DataSource source, long hash) throws IOException {
        if (!model.header().streamable()) {
            return readEmbeddedGeometry(model, source);
        }
        if (readStreams) {
            return readStreamedGeometry(model, 0, hash);
        }
        return List.of();
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
