package be.twofold.valen.reader.model;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.geometry.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class ModelReader {
    private final FileManager fileManager;

    public ModelReader(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Model read(BetterBuffer buffer) throws IOException {
        return read(buffer, false, 0);
    }

    public Model read(BetterBuffer buffer, boolean readStreams, long hash) throws IOException {
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
            assert meshInfo.lods().size() == 1;
            meshes.add(readEmbeddedMesh(meshInfo.lods().getFirst(), buffer));
        }
        return meshes;
    }

    private static Mesh readEmbeddedMesh(ModelLodInfo lodInfo, BetterBuffer buffer) {
        var vertices = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        var texCoords = lodInfo.flags() != 0x0801d ? FloatBuffer.allocate(lodInfo.numVertices() * 2) : null;
        var normals = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        var tangents = FloatBuffer.allocate(lodInfo.numVertices() * 4);
        var indices = ShortBuffer.allocate(lodInfo.numEdges() * 2);

        for (var i = 0; i < lodInfo.numVertices(); i++) {
            Geometry.readVertex(buffer, vertices, lodInfo.vertexOffset(), lodInfo.vertexScale());
            if (lodInfo.flags() != 0x0801d) {
                Geometry.readUV(buffer, texCoords, lodInfo.uvOffset(), lodInfo.uvScale());
            }

            Geometry.readPackedNormal(buffer, normals);
            Geometry.readPackedTangent(buffer, tangents);
            buffer.expectInt(-1);

            buffer.skip(8); // skip lightmap UVs

            if (lodInfo.flags() == 0x1801f) {
                buffer.expectInt(-1);
                buffer.expectInt(0);
            }
        }

        indices.put(buffer.getShorts(lodInfo.numEdges()));
        return new Mesh(vertices.flip(), normals.flip(), tangents.flip(), texCoords, null, null, null, indices);
    }

    private List<Mesh> readStreamedGeometry(Model model, int lod, long hash) {
        var streamHash = (hash << 4) | lod;
        var diskLayout = model.streamDiskLayouts().get(lod);
        var uncompressedSize = diskLayout.uncompressedSize();

        var buffer = fileManager.readStream(streamHash, uncompressedSize);
        var lods = model.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lods().get(lod))
            .toList();
        var layouts = model.streamDiskLayouts().get(lod).memoryLayouts();

        return new GeometryReader(false).readMeshes(buffer, lods, layouts);
    }
}
