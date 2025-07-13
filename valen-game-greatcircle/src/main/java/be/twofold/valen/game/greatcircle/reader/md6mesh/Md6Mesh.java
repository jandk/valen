package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;
import java.util.*;

record Md6Mesh(
        Md6MeshHeader header,
        Md6MeshBoneInfo boneInfo,
        List<Md6MeshInfo> meshInfos,
        List<Md6MeshMaterialInfo> materialInfos,
        Md6MeshGeoDecals geoDecals,
        List<GeometryDiskLayout> layouts
) {
    static Md6Mesh read(BinaryReader reader) throws IOException {
        var header = Md6MeshHeader.read(reader);
        var boneInfo = Md6MeshBoneInfo.read(reader);
        var meshInfos = reader.readObjects(reader.readInt(), Md6MeshInfo::read);
        var materialInfos = reader.readObjects(reader.readInt(), Md6MeshMaterialInfo::read);
        var geoDecals = Md6MeshGeoDecals.read(reader);
        var memoryLayouts = reader.readObjects(5, GeometryMemoryLayout::read);
        int padding = reader.readInt();
        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var i = 0; i < 5; i++) {
            var subMemoryLayouts = List.copyOf(memoryLayouts.subList(i, i + 1));
            layouts.add(GeometryDiskLayout.read(reader, subMemoryLayouts));
        }

        reader.expectEnd();
        return new Md6Mesh(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts);
    }
}
