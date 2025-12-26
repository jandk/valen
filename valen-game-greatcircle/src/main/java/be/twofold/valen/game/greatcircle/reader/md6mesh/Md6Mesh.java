package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;

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
    static Md6Mesh read(BinarySource source) throws IOException {
        var header = Md6MeshHeader.read(source);
        var boneInfo = Md6MeshBoneInfo.read(source);
        var meshInfos = source.readObjects(source.readInt(), Md6MeshInfo::read);
        var materialInfos = source.readObjects(source.readInt(), Md6MeshMaterialInfo::read);
        var geoDecals = Md6MeshGeoDecals.read(source);
        var memoryLayouts = source.readObjects(5, GeometryMemoryLayout::read);
        int padding = source.readInt();
        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var i = 0; i < 5; i++) {
            var subMemoryLayouts = List.copyOf(memoryLayouts.subList(i, i + 1));
            layouts.add(GeometryDiskLayout.read(source, subMemoryLayouts));
        }

        source.expectEnd();
        return new Md6Mesh(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts);
    }
}
