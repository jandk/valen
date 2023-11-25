package be.twofold.valen.reader.md6;

import be.twofold.valen.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.reader.resource.*;

import java.nio.*;
import java.util.*;

public final class Md6Reader {
    private final BetterBuffer buffer;
    private final FileManager fileManager;
    private final ResourcesEntry entry;

    private Md6BoneInfo boneInfo;
    private List<Md6MeshInfo> meshInfos;
    private List<GeometryMemoryLayout> memoryLayouts;
    private List<GeometryDiskLayout> diskLayouts;
    private List<Mesh> meshes;

    public Md6Reader(BetterBuffer buffer, FileManager fileManager, ResourcesEntry entry) {
        this.buffer = buffer;
        this.fileManager = fileManager;
        this.entry = entry;
    }

    public static Md6 read(BetterBuffer buffer, FileManager fileManager, ResourcesEntry entry) {
        return new Md6Reader(buffer, fileManager, entry).read();
    }

    private Md6 read() {
        Md6Header header = Md6Header.read(buffer);
        boneInfo = Md6BoneInfo.read(buffer);
        meshInfos = buffer.getStructs(buffer.getInt(), Md6MeshInfo::read);
        List<Md6MaterialInfo> materialInfos = buffer.getStructs(buffer.getInt(), Md6MaterialInfo::read);

        skipGeoDecals();
        buffer.expectInt(5);

        memoryLayouts = buffer.getStructs(5, GeometryMemoryLayout::read);
        diskLayouts = buffer.getStructs(5, GeometryDiskLayout::read);

        buffer.expectEnd();

        meshes = readStreamedGeometry(0);

        // Apply the lookup table for the joint indices
        fixJointIndices();

        return new Md6(header, boneInfo, meshInfos, materialInfos, memoryLayouts, diskLayouts, meshes);
    }

    private void skipGeoDecals() {
        String geoDecalName = buffer.getString();
        if (!geoDecalName.isEmpty()) {
            throw new UnsupportedOperationException("GeoDecals are not supported");
        }
        int numGeoDecalStreams = buffer.getInt();
        int numGeoDecalElements = 0;
        for (int i = 0; i < numGeoDecalStreams; i++) {
            numGeoDecalElements += buffer.getInt();
        }
        buffer.skip(numGeoDecalElements * 4);
    }

    private List<Mesh> readStreamedGeometry(int lod) {
        long hash = (entry.defaultHash() << 4) | lod;
        int size = diskLayouts.get(lod).uncompressedSize();

        BetterBuffer buffer = fileManager.readStream(hash, size);
        List<LodInfo> lodInfos = meshInfos.stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        List<GeometryMemoryLayout> layouts = List.of(memoryLayouts.get(lod));

        return new GeometryReader(true).readMeshes(buffer, lodInfos, layouts);
    }

    private void fixJointIndices() {
        byte[] bones = boneInfo.bones();

        // This lookup table is in reverse... Nice
        byte[] lookup = new byte[bones.length];
        for (int i = 0; i < bones.length; i++) {
            lookup[Byte.toUnsignedInt(bones[i])] = (byte) i;
        }

        for (int i = 0; i < meshInfos.size(); i++) {
            Md6MeshInfo meshInfo = meshInfos.get(i);
            ByteBuffer joints = meshes.get(i).joints();

            byte[] array = joints.array();
            for (int j = 0; j < array.length; j++) {
                array[j] = lookup[(array[j] & 0xff) + meshInfo.unknown2()];
            }
        }
    }
}
