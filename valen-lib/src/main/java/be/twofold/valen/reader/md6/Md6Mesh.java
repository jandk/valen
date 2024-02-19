package be.twofold.valen.reader.md6;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.geometry.*;

import java.util.*;

public record Md6Mesh(
    Md6MeshHeader header,
    Md6MeshBoneInfo boneInfo,
    List<Md6MeshInfo> meshInfos,
    List<Md6MeshMaterialInfo> materialInfos,
    Md6MeshGeoDecals geoDecals,
    List<GeometryDiskLayout> layouts,
    List<Mesh> meshes
) {
    public static Md6Mesh read(BetterBuffer buffer) {
        var header = Md6MeshHeader.read(buffer);
        var boneInfo = Md6MeshBoneInfo.read(buffer);
        var meshInfos = buffer.getStructs(buffer.getInt(), Md6MeshInfo::read);
        var materialInfos = buffer.getStructs(buffer.getInt(), Md6MeshMaterialInfo::read);
        var geoDecals = Md6MeshGeoDecals.read(buffer);
        var memoryLayouts = buffer.getStructs(buffer.getInt(), GeometryMemoryLayout::read);

        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var i = 0; i < 5; i++) {
            var subMemoryLayouts = List.copyOf(memoryLayouts.subList(i, i + 1));
            layouts.add(GeometryDiskLayout.read(buffer, subMemoryLayouts));
        }

        buffer.expectEnd();
        return new Md6Mesh(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts, List.of());
    }

    public Md6Mesh withMeshes(List<Mesh> meshes) {
        return new Md6Mesh(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts, meshes);
    }
}
