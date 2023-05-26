package be.twofold.valen.reader.md6;

import be.twofold.valen.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.reader.resource.*;

import java.util.*;

public final class Md6Reader {
    private final BetterBuffer buffer;
    private final StreamLoader loader;
    private final ResourcesEntry entry;

    public Md6Reader(BetterBuffer buffer, StreamLoader loader, ResourcesEntry entry) {
        this.buffer = buffer;
        this.loader = loader;
        this.entry = entry;
    }

    public Md6 read() {
        Md6Header header = Md6Header.read(buffer);
        Md6BoneInfo boneInfo = Md6BoneInfo.read(buffer);
        List<Md6MeshInfo> meshInfos = buffer.getStructs(buffer.getInt(), Md6MeshInfo::read);
        List<Md6MaterialInfo> materialInfos = buffer.getStructs(buffer.getInt(), Md6MaterialInfo::read);

        skipGeoDecals();
        buffer.expectInt(5);

        List<GeometryMemoryLayout> memoryLayouts = buffer.getStructs(5, GeometryMemoryLayout::read);
        List<GeometryDiskLayout> diskLayouts = buffer.getStructs(5, GeometryDiskLayout::read);

        buffer.expectEnd();
        return new Md6(header, boneInfo, meshInfos, materialInfos, memoryLayouts, diskLayouts);
    }

    private void skipGeoDecals() {
        String geoDecalName = buffer.getString();
        int numGeoDecalStreams = buffer.getInt();
        int numGeoDecalElements = 0;
        for (int i = 0; i < numGeoDecalStreams; i++) {
            numGeoDecalElements += buffer.getInt();
        }
        buffer.skip(numGeoDecalElements * 4);
    }
}
