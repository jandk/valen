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
    private final StreamLoader streamLoader;
    private final ResourcesEntry entry;

    private ModelHeader header;
    private final List<ModelMeshInfo> meshInfos = new ArrayList<>();
    private final List<List<ModelLodInfo>> lodInfos = new ArrayList<>();
    private ModelSettings settings;
    private ModelBooleans booleans;
    private final List<List<GeometryMemoryLayout>> streamMemLayouts = new ArrayList<>();
    private final List<GeometryDiskLayout> streamDiskLayouts = new ArrayList<>();

    public ModelReader(ByteBuffer buffer, StreamLoader streamLoader, ResourcesEntry entry) {
        this.buffer = new BetterBuffer(buffer);
        this.streamLoader = streamLoader;
        this.entry = entry;
    }

    public Model read() throws IOException {
        header = ModelHeader.read(buffer);
        readMeshesAndLods();
        settings = ModelSettings.read(buffer);
        readGeoDecals();
        booleans = ModelBooleans.read(buffer);
        buffer.skip(header.numMeshes() * LodCount);

        if (header.streamed()) {
            readStreamInfo();
        } else {
            readEmbeddedGeometry();
        }

        return new Model(header, meshInfos, lodInfos, settings, booleans, streamMemLayouts, streamDiskLayouts);
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
            vertices.put(buffer.getFloat()); // x
            vertices.put(buffer.getFloat()); // y
            vertices.put(buffer.getFloat()); // z

            if (lodInfo.flags() != 0x0801d) {
                texCoords.put(buffer.getFloat()); // u
                texCoords.put(buffer.getFloat()); // v
            }

            readPackedNormal(buffer, normals);
            readPackedTangent(buffer, tangents);
            buffer.expectInt(-1);

            buffer.skip(8); // skip lightmap UVs

            if (lodInfo.flags() == 0x1801f) {
                buffer.expectInt(-1);
                buffer.expectInt(0);
            }
        }

        indices.put(buffer.getShorts(lodInfo.numEdges()));
        return new Mesh(vertices, normals, tangents, texCoords, indices);
    }

    public static void readPackedNormal(BetterBuffer src, FloatBuffer dst) {
        float packedXn = Byte.toUnsignedInt(src.getByte());
        float packedYn = Byte.toUnsignedInt(src.getByte());
        float packedZn = Byte.toUnsignedInt(src.getByte());
        src.skip(1);

        float x = (packedXn / 255) * 2 - 1;
        float y = (packedYn / 255) * 2 - 1;
        float z = (packedZn / 255) * 2 - 1;

        // Normalize, as we have low accuracy
        float scale = (float) (1 / Math.sqrt(x * x + y * y + z * z));
        dst.put(x * scale);
        dst.put(y * scale);
        dst.put(z * scale);
    }

    public static void readPackedTangent(BetterBuffer src, FloatBuffer dst) {
        float packedXn = Byte.toUnsignedInt(src.getByte());
        float packedYn = Byte.toUnsignedInt(src.getByte());
        float packedZn = Byte.toUnsignedInt(src.getByte());
        float w = src.getByte() == 0 ? 1 : -1;

        float x = (packedXn / 255) * 2 - 1;
        float y = (packedYn / 255) * 2 - 1;
        float z = (packedZn / 255) * 2 - 1;

        // Normalize, as we have low accuracy
        float scale = (float) (1 / Math.sqrt(x * x + y * y + z * z));
        dst.put(x * scale);
        dst.put(y * scale);
        dst.put(z * scale);
        dst.put(w);
    }
}
