package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.geometry.*;

import java.util.*;

public record Md6Model(
    Md6ModelHeader header,
    Md6ModelBoneInfo boneInfo,
    List<Md6ModelInfo> meshInfos,
    List<Md6ModelMaterialInfo> materialInfos,
    Md6ModelGeoDecals geoDecals,
    List<GeometryDiskLayout> layouts,
    List<Mesh> meshes
) {
    public static Md6Model read(BetterBuffer buffer) {
        var header = Md6ModelHeader.read(buffer);
        var boneInfo = Md6ModelBoneInfo.read(buffer);
        var meshInfos = buffer.getStructs(buffer.getInt(), Md6ModelInfo::read);
        var materialInfos = buffer.getStructs(buffer.getInt(), Md6ModelMaterialInfo::read);
        var geoDecals = Md6ModelGeoDecals.read(buffer);
        var memoryLayouts = buffer.getStructs(buffer.getInt(), GeometryMemoryLayout::read);

        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var i = 0; i < 5; i++) {
            var subMemoryLayouts = List.copyOf(memoryLayouts.subList(i, i + 1));
            layouts.add(GeometryDiskLayout.read(buffer, subMemoryLayouts));
        }

        buffer.expectEnd();
        return new Md6Model(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts, List.of());
    }

    public Md6Model withMeshes(List<Mesh> meshes) {
        return new Md6Model(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts, meshes);
    }
}
