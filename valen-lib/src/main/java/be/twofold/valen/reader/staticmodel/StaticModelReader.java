package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.resource.*;
import be.twofold.valen.stream.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class StaticModelReader implements ResourceReader<Model> {
    private final StreamManager streamManager;
    private final Provider<FileManager> fileManagerProvider;
    private final boolean readStreams;
    private final boolean readMaterials;

    @Inject
    public StaticModelReader(
        StreamManager streamManager,
        Provider<FileManager> fileManagerProvider
    ) {
        this(streamManager, fileManagerProvider, true, true);
    }

    StaticModelReader(
        StreamManager streamManager,
        Provider<FileManager> fileManagerProvider,
        boolean readStreams,
        boolean readMaterials
    ) {
        this.streamManager = Objects.requireNonNull(streamManager);
        this.fileManagerProvider = Objects.requireNonNull(fileManagerProvider);
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
        if (readStreams) {
            var meshes = readMeshes(model, source, hash);
            model = model.withMeshes(meshes);
            source.expectEnd();
        }

        if (readMaterials) {
            var materials = new LinkedHashMap<String, Material>();
            var materialIndices = new HashMap<String, Integer>();

            var meshes = new ArrayList<Mesh>();
            for (int i = 0; i < model.meshes().size(); i++) {
                var meshInfo = model.meshInfos().get(i);
                var materialName = meshInfo.mtlDecl();
                var materialFile = "generated/decls/material2/" + materialName + ".decl";
                var materialIndex = materialIndices.computeIfAbsent(materialName, k -> materials.size());
                materials.computeIfAbsent(materialName, name -> fileManagerProvider.get().readResource(FileType.Material, materialFile));
                meshes.add(model.meshes().get(i).withMaterialIndex(materialIndex));
            }
            model = model
                .withMeshes(meshes)
                .withMaterials(List.copyOf(materials.values()));
        }
        return model;
    }

    // region Meshes

    private List<Mesh> readMeshes(StaticModel model, DataSource source, long hash) throws IOException {
        if (!model.header().streamable()) {
            return readEmbeddedGeometry(model, source);
        }
        return readStreamedGeometry(model, 0, hash);
    }

    private List<Mesh> readEmbeddedGeometry(StaticModel model, DataSource source) throws IOException {
        List<Mesh> meshes = new ArrayList<>();
        for (var meshInfo : model.meshInfos()) {
            assert meshInfo.lodInfos().size() == 1;
            meshes.add(readEmbeddedMesh(meshInfo.lodInfos().getFirst(), source));
        }
        return meshes;
    }

    private Mesh readEmbeddedMesh(StaticModelLodInfo lodInfo, DataSource source) throws IOException {
        var vertices = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        var texCoords = lodInfo.flags() != 0x0801d ? FloatBuffer.allocate(lodInfo.numVertices() * 2) : null;
        var normals = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        var tangents = FloatBuffer.allocate(lodInfo.numVertices() * 4);
        var indices = ShortBuffer.allocate(lodInfo.numEdges());

        for (var i = 0; i < lodInfo.numVertices(); i++) {
            Geometry.readVertex(source, vertices, lodInfo.vertexOffset(), lodInfo.vertexScale());
            if (lodInfo.flags() != 0x0801d) {
                Geometry.readUV(source, texCoords, lodInfo.uvOffset(), lodInfo.uvScale());
            }

            Geometry.readPackedNormal(source, normals);
            source.skip(-8);
            Geometry.readPackedTangent(source, tangents);
            source.expectInt(-1);

            source.skip(8); // skip lightmap UVs

            if (lodInfo.flags() == 0x1801f) {
                source.expectInt(-1);
                source.expectInt(0);
            }
        }

        for (var i = 0; i < lodInfo.numEdges(); i++) {
            indices.put(source.readShort());
        }

        var faceBuffer = new VertexBuffer(indices.flip(), ElementType.Scalar, ComponentType.UnsignedShort, false);
        var vertexBuffers = new EnumMap<Semantic, VertexBuffer>(Semantic.class);
        vertexBuffers.put(Semantic.Position, new VertexBuffer(vertices.flip(), ElementType.Vector3, ComponentType.Float, false));
        vertexBuffers.put(Semantic.Normal, new VertexBuffer(normals.flip(), ElementType.Vector3, ComponentType.Float, false));
        vertexBuffers.put(Semantic.Tangent, new VertexBuffer(tangents.flip(), ElementType.Vector4, ComponentType.Float, false));
        if (texCoords != null) {
            vertexBuffers.put(Semantic.TexCoord, new VertexBuffer(texCoords.flip(), ElementType.Vector2, ComponentType.Float, false));
        }
        return new Mesh(faceBuffer, vertexBuffers, -1);
    }

    private List<Mesh> readStreamedGeometry(StaticModel model, int lod, long hash) throws IOException {
        var streamHash = (hash << 4) | lod;
        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();

        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = model.streamDiskLayouts().get(lod).memoryLayouts();

        var source = new ByteArrayDataSource(streamManager.read(streamHash, uncompressedSize));
        return new GeometryReader(false)
            .readMeshes(source, lods, layouts);
    }

    // endregion

}
