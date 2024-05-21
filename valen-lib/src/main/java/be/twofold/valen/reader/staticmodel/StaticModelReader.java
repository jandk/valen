package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.resource.*;
import dagger.*;
import jakarta.inject.*;

import java.io.*;
import java.util.*;

public final class StaticModelReader implements ResourceReader<Model> {
    private final Lazy<FileManager> fileManager;
    private final boolean readStreams;
    private final boolean readMaterials;

    @Inject
    StaticModelReader(Lazy<FileManager> fileManager) {
        this(fileManager, true, true);
    }

    StaticModelReader(
        Lazy<FileManager> fileManager,
        boolean readStreams,
        boolean readMaterials
    ) {
        this.fileManager = fileManager;
        this.readStreams = readStreams;
        this.readMaterials = readMaterials;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.Model;
    }

    @Override
    public Model read(DataSource source, Resource resource) throws IOException {
        var model = read(source, resource.hash());
        return new Model(model.meshes(), model.materials(), null);
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
                materials.computeIfAbsent(materialName, name -> fileManager.get().readResource(FileType.Material, materialFile));
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

        var source = new ByteArrayDataSource(fileManager.get().readStream(streamHash, uncompressedSize));
        return GeometryReader.readStreamedMesh(source, lods, layouts, false);
    }

}
