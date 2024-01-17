package be.twofold.valen.reader.model;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.resource.*;
import be.twofold.valen.stream.*;

import java.nio.*;
import java.util.*;

public final class ModelReader implements ResourceReader<be.twofold.valen.core.geometry.Model> {
    private final StreamManager streamManager;

    public ModelReader(StreamManager streamManager) {
        this.streamManager = streamManager;
    }

    @Override
    public be.twofold.valen.core.geometry.Model read(BetterBuffer buffer, Resource resource) {
        Model model = read(buffer, true, resource.hash());
        return new be.twofold.valen.core.geometry.Model(model.meshes(), null);
    }

    public Model read(BetterBuffer buffer, boolean readStreams, long hash) {
        var model = Model.read(buffer);

        List<Mesh> meshes;
        if (model.header().streamed()) {
            meshes = readStreams ? readStreamedGeometry(model, 0, hash) : List.of();
        } else {
            meshes = readEmbeddedGeometry(model, buffer);
        }

        buffer.expectEnd();
        return model.withMeshes(meshes);
    }

    private static List<Mesh> readEmbeddedGeometry(Model model, BetterBuffer buffer) {
        List<Mesh> meshes = new ArrayList<>();
        for (var meshInfo : model.meshInfos()) {
            assert meshInfo.lodInfos().size() == 1;
            meshes.add(readEmbeddedMesh(meshInfo.lodInfos().getFirst(), buffer));
        }
        return meshes;
    }

    private static Mesh readEmbeddedMesh(ModelLodInfo lodInfo, BetterBuffer buffer) {
        var vertices = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        var texCoords = lodInfo.flags() != 0x0801d ? FloatBuffer.allocate(lodInfo.numVertices() * 2) : null;
        var normals = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        var tangents = FloatBuffer.allocate(lodInfo.numVertices() * 4);
        var indices = ShortBuffer.allocate(lodInfo.numEdges());

        for (var i = 0; i < lodInfo.numVertices(); i++) {
            Geometry.readVertex(buffer, vertices, lodInfo.vertexOffset(), lodInfo.vertexScale());
            if (lodInfo.flags() != 0x0801d) {
                Geometry.readUV(buffer, texCoords, lodInfo.uvOffset(), lodInfo.uvScale());
            }

            Geometry.readPackedNormal(buffer, normals);
            buffer.skip(-8);
            Geometry.readPackedTangent(buffer, tangents);
            buffer.expectInt(-1);

            buffer.skip(8); // skip lightmap UVs

            if (lodInfo.flags() == 0x1801f) {
                buffer.expectInt(-1);
                buffer.expectInt(0);
            }
        }

        for (int i = 0; i < lodInfo.numEdges(); i++) {
            indices.put(buffer.getShort());
        }

        var faceBuffer = new VertexBuffer(indices.flip(), ElementType.Scalar, ComponentType.UnsignedShort, false);
        var vertexBuffers = new EnumMap<Semantic, VertexBuffer>(Semantic.class);
        vertexBuffers.put(Semantic.Position, new VertexBuffer(vertices.flip(), ElementType.Vector3, ComponentType.Float, false));
        vertexBuffers.put(Semantic.Normal, new VertexBuffer(normals.flip(), ElementType.Vector3, ComponentType.Float, false));
        vertexBuffers.put(Semantic.Tangent, new VertexBuffer(tangents.flip(), ElementType.Vector4, ComponentType.Float, false));
        if (texCoords != null) {
            vertexBuffers.put(Semantic.TexCoord, new VertexBuffer(texCoords.flip(), ElementType.Vector2, ComponentType.Float, false));
        }
        return new Mesh(faceBuffer, vertexBuffers);
    }

    private List<Mesh> readStreamedGeometry(Model model, int lod, long hash) {
        var streamHash = (hash << 4) | lod;
        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();

        var buffer = BetterBuffer.wrap(streamManager.read(streamHash, uncompressedSize));
        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = model.streamDiskLayouts().get(lod).memoryLayouts();

        return new GeometryReader(false).readMeshes(buffer, lods, layouts);
    }
}
