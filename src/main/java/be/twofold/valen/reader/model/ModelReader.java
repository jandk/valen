package be.twofold.valen.reader.model;

import be.twofold.valen.*;
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
}
