package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.*;
import be.twofold.valen.game.greatcircle.reader.geometry.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.io.*;
import java.util.*;

public final class StaticModelReader implements ResourceReader<Model> {
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
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.model;
    }

    @Override
    public Model read(DataSource source, Asset asset) throws IOException {
        long hash = (Long) asset.properties().get("hash");
        var model = StaticModel.read(source, (Integer) asset.properties().get("version"));
        var meshes = new ArrayList<>(readMeshes(model, source, hash));

        if (readMaterials) {
            var materials = new HashMap<String, Material>();
            for (int i = 0; i < meshes.size(); i++) {
                var meshInfo = model.meshInfos().get(i);
                var materialName = meshInfo.mtlDecl();
                if (!materials.containsKey(materialName)) {
                    var assetId = ResourceKey.from(materialName, ResourceType.material2);
                    var material = archive.loadAsset(assetId, Material.class);
                    materials.put(materialName, material);
                }
                meshes.set(i, meshes.get(i)
                    .withMaterial(materials.get(materialName)));
            }
        }
        return new Model(asset.id().fullName(), meshes, null);
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
        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();

        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();
        var bytes = archive.readStream(diskLayout.hash(), uncompressedSize);
        var source = DataSource.fromArray(bytes);
        return GeometryReader.readStreamedMesh(source, lods, diskLayout.memoryLayouts(), false);
    }

}
