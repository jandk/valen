package be.twofold.valen.reader.md6;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.reader.resource.*;

import java.nio.*;
import java.util.*;
import java.util.stream.*;

public final class Md6Reader {
    private final BetterBuffer buffer;
    private final StreamLoader loader;
    private final ResourcesEntry entry;
    private Md6BoneInfo boneInfo;
    private List<Md6MeshInfo> meshInfos;
    private List<GeometryMemoryLayout> memoryLayouts;
    private List<GeometryDiskLayout> diskLayouts;
    private List<Mesh> meshes;

    public Md6Reader(BetterBuffer buffer, StreamLoader loader, ResourcesEntry entry) {
        this.buffer = buffer;
        this.loader = loader;
        this.entry = entry;
    }

    public Md6 read() {
        return read(false);
    }

    public Md6 read(boolean readMeshes) {
        Md6Header header = Md6Header.read(buffer);
        boneInfo = Md6BoneInfo.read(buffer);
        meshInfos = buffer.getStructs(buffer.getInt(), Md6MeshInfo::read);
        List<Md6MaterialInfo> materialInfos = buffer.getStructs(buffer.getInt(), Md6MaterialInfo::read);

        skipGeoDecals();
        buffer.expectInt(5);

        memoryLayouts = buffer.getStructs(5, GeometryMemoryLayout::read);
        diskLayouts = buffer.getStructs(5, GeometryDiskLayout::read);

        buffer.expectEnd();

        meshes = readMeshes ? readStreamedGeometry(0) : List.of();

        // Apply the lookup table for the joint indices
        fixJointIndices();

        return new Md6(header, boneInfo, meshInfos, materialInfos, memoryLayouts, diskLayouts, meshes);
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

    private List<Mesh> readStreamedGeometry(int lod) {
        long hash = (entry.streamResourceHash() << 4) | lod;
        int size = diskLayouts.get(lod).uncompressedSize();

        BetterBuffer buffer = loader.load(hash, size)
            .map(BetterBuffer::wrap)
            .orElseThrow();
        List<LodInfo> lods = meshInfos.stream()
            .map(mi -> mi.lodInfos().get(0))
            .collect(Collectors.toUnmodifiableList());
        List<GeometryMemoryLayout> layouts = List.of(memoryLayouts.get(lod));

        return new GeometryReader(true).readMeshes(buffer, lods, layouts);
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
            ByteBuffer colors = meshes.get(i).colors();

            byte[] array = colors.array();
            for (int j = 0; j < array.length; j++) {
                array[j] = lookup[(array[j] & 0xff) + meshInfo.unknown2()];
            }
        }
    }
}
