package be.twofold.valen.reader.model;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.reader.resource.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class ModelReader {
    private static final int LodCount = 5;

    private final BetterBuffer buffer;
    private final FileManager fileManager;
    private final ResourcesEntry entry;

    private ModelHeader header;
    private final List<ModelMeshInfo> meshInfos = new ArrayList<>();
    private final List<List<ModelLodInfo>> lodInfos = new ArrayList<>();
    private final List<List<GeometryMemoryLayout>> streamMemLayouts = new ArrayList<>();
    private final List<GeometryDiskLayout> streamDiskLayouts = new ArrayList<>();

    public ModelReader(BetterBuffer buffer, FileManager fileManager, ResourcesEntry entry) {
        this.buffer = buffer;
        this.fileManager = fileManager;
        this.entry = entry;
    }

    public Model read() throws IOException {
        return read(true);
    }

    public Model read(boolean readMeshes) throws IOException {
        header = ModelHeader.read(buffer);
        readMeshesAndLods();
        ModelSettings settings = ModelSettings.read(buffer);
        readGeoDecals();
        ModelBooleans booleans = ModelBooleans.read(buffer);
        buffer.skip(header.numMeshes() * LodCount);

        List<Mesh> meshes;
        if (header.streamed()) {
            if (readMeshes) {
                readStreamInfo();
                meshes = readStreamedGeometry(0);
            } else {
                meshes = List.of();
            }
        } else {
            meshes = readEmbeddedGeometry();
        }

        return new Model(header, meshInfos, lodInfos, settings, booleans, streamMemLayouts, streamDiskLayouts, meshes);
    }

    private void readMeshesAndLods() {
        for (int mesh = 0; mesh < header.numMeshes(); mesh++) {
            meshInfos.add(ModelMeshInfo.read(buffer));

            lodInfos.add(new ArrayList<>());
            for (int lod = 0; lod < LodCount; lod++) {
                if (!buffer.getIntAsBool()) {
                    lodInfos.get(mesh).add(ModelLodInfo.read(buffer));
                }
            }
        }
    }

    private void readGeoDecals() {
        int numGeoDecals = buffer.getInt();
        List<ModelGeoDecalProjection> geoDecalProjections = new ArrayList<>();
        for (int i = 0; i < numGeoDecals; i++) {
            geoDecalProjections.add(ModelGeoDecalProjection.read(buffer));
        }
        String geoDecalMaterialName = buffer.getString();
    }

    private void readStreamInfo() {
        for (int lod = 0; lod < LodCount; lod++) {
            int numStreams = buffer.getInt();
            streamMemLayouts.add(new ArrayList<>());
            for (int stream = 0; stream < numStreams; stream++) {
                streamMemLayouts.get(lod).add(GeometryMemoryLayout.read(buffer));
            }
            streamDiskLayouts.add(GeometryDiskLayout.read(buffer));
        }
        buffer.expectEnd();
    }

    private List<Mesh> readEmbeddedGeometry() {
        List<Mesh> meshes = new ArrayList<>();
        for (int i = 0; i < meshInfos.size(); i++) {
            List<ModelLodInfo> lods = lodInfos.get(i);
            assert lods.size() == 1;
            meshes.add(readEmbeddedMesh(lods.get(0)));
        }
        buffer.expectEnd();
        return meshes;
    }

    private Mesh readEmbeddedMesh(ModelLodInfo lodInfo) {
        FloatBuffer vertices = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        FloatBuffer texCoords = lodInfo.flags() != 0x0801d ? FloatBuffer.allocate(lodInfo.numVertices() * 2) : null;
        FloatBuffer normals = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        FloatBuffer tangents = FloatBuffer.allocate(lodInfo.numVertices() * 4);
        ShortBuffer indices = ShortBuffer.allocate(lodInfo.numEdges() * 2);

        for (int i = 0; i < lodInfo.numVertices(); i++) {
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
        return new Mesh(vertices, normals, tangents, texCoords, null, null, indices);
    }

    private List<Mesh> readStreamedGeometry(int lod) {
        long hash = (entry.defaultHash() << 4) | lod;
        int size = streamDiskLayouts.get(lod).uncompressedSize();

        BetterBuffer buffer = fileManager.readStream(hash, size);
        List<LodInfo> lods = lodInfos.stream()
            .<LodInfo>map(l -> l.get(lod))
            .toList();
        List<GeometryMemoryLayout> layouts = streamMemLayouts.get(lod);

        return new GeometryReader(false).readMeshes(buffer, lods, layouts);
    }
}
