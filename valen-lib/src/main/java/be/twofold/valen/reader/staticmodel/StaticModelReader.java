package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.resource.*;
import be.twofold.valen.stream.*;
import jakarta.inject.*;

import java.io.*;
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

        model = model.withMeshes(readMeshes(model, source, hash));
        source.expectEnd();

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
            meshes.add(readEmbeddedMesh(meshInfo.lodInfos().getFirst(), source));
        }
        return meshes;
    }

    private Mesh readEmbeddedMesh(StaticModelLodInfo lodInfo, DataSource source) throws IOException {
        var masks = GeometryVertexMask.FixedOrder.stream()
            .filter(mask -> (lodInfo.flags() & mask.mask()) == mask.mask())
            .toList();

        var stride = masks.stream()
            .mapToInt(GeometryVertexMask::size)
            .sum();

        var offset = 0;
        var accessors = new ArrayList<Geo.Accessor>();

        for (GeometryVertexMask mask : masks) {
            var finalOffset = offset;
            buildAccessor(mask).stream()
                .map(info -> {
                    var reader = reader(mask, info.semantic(), lodInfo);
                    return new Geo.Accessor(finalOffset, lodInfo.numVertices(), stride, info, reader);
                })
                .forEach(accessors::add);
            offset += mask.size();
        }

        offset += stride * (lodInfo.numVertices() - 1);
        var faceInfo = new VertexBuffer.Info(null, ElementType.Scalar, ComponentType.UnsignedShort, false);
        var faceAccessor = new Geo.Accessor(offset, lodInfo.numEdges(), 2, faceInfo, Geometry.readFace());

        return Geo.readMesh(source, accessors, faceAccessor);
    }

    private List<VertexBuffer.Info> buildAccessor(GeometryVertexMask mask) {
        return switch (mask) {
            case WGVS_POSITION_SHORT, WGVS_POSITION -> List.of(
                new VertexBuffer.Info(Semantic.Position, ElementType.Vector3, ComponentType.Float, false)
            );
            case WGVS_NORMAL_TANGENT -> List.of(
                new VertexBuffer.Info(Semantic.Normal, ElementType.Vector3, ComponentType.Float, false),
                new VertexBuffer.Info(Semantic.Tangent, ElementType.Vector4, ComponentType.Float, false)
            );
            case WGVS_LIGHTMAP_UV_SHORT, WGVS_LIGHTMAP_UV -> List.of(
                new VertexBuffer.Info(Semantic.TexCoord1, ElementType.Vector2, ComponentType.Float, false)
            );
            case WGVS_MATERIAL_UV_SHORT, WGVS_MATERIAL_UV -> List.of(
                new VertexBuffer.Info(Semantic.TexCoord0, ElementType.Vector2, ComponentType.Float, false)
            );
            case WGVS_COLOR -> List.of(
                new VertexBuffer.Info(Semantic.Color0, ElementType.Vector4, ComponentType.UnsignedByte, true)
            );
            case WGVS_MATERIALS -> List.of();
        };
    }

    private Geo.Reader reader(GeometryVertexMask mask, Semantic semantic, LodInfo lodInfo) {
        return switch (mask) {
            case WGVS_POSITION_SHORT -> Geometry.readPackedPosition(lodInfo.vertexOffset(), lodInfo.vertexScale());
            case WGVS_POSITION -> Geometry.readPosition(lodInfo.vertexOffset(), lodInfo.vertexScale());
            case WGVS_NORMAL_TANGENT -> switch (semantic) {
                case Semantic.Normal() -> Geometry.readPackedNormal();
                case Semantic.Tangent() -> Geometry.readPackedTangent();
                case Semantic.Weights(int ignored) -> Geometry.readWeight();
                default -> throw new IllegalStateException("Unexpected value: " + semantic);
            };
            case WGVS_LIGHTMAP_UV_SHORT, WGVS_MATERIAL_UV_SHORT -> Geometry.readPackedUV(lodInfo.uvOffset(), lodInfo.uvScale());
            case WGVS_LIGHTMAP_UV, WGVS_MATERIAL_UV -> Geometry.readUV(lodInfo.uvOffset(), lodInfo.uvScale());
            case WGVS_COLOR -> Geometry.readColor();
            case WGVS_MATERIALS -> throw new UnsupportedOperationException();
        };
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
            .readStreamedMeshes(source, lods, layouts);
    }

}
