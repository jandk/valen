package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.game.eternal.reader.geometry.*;

import java.io.*;
import java.util.*;

public record Md6Model(
    Md6ModelHeader header,
    Md6ModelBoneInfo boneInfo,
    List<Md6ModelInfo> meshInfos,
    List<Md6ModelMaterialInfo> materialInfos,
    Md6ModelGeoDecals geoDecals,
    List<GeometryDiskLayout> layouts,
    List<Mesh> meshes,
    List<Material> materials
) {
    public static Md6Model read(DataSource source) throws IOException {
        var header = Md6ModelHeader.read(source);
        var boneInfo = Md6ModelBoneInfo.read(source);
        var meshInfos = source.readStructs(source.readInt(), Md6ModelInfo::read);
        var materialInfos = source.readStructs(source.readInt(), Md6ModelMaterialInfo::read);
        var geoDecals = Md6ModelGeoDecals.read(source);
        var memoryLayouts = source.readStructs(source.readInt(), GeometryMemoryLayout::read);

        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var i = 0; i < 5; i++) {
            var subMemoryLayouts = List.copyOf(memoryLayouts.subList(i, i + 1));
            layouts.add(GeometryDiskLayout.read(source, subMemoryLayouts));
        }

        source.expectEnd();
        return new Md6Model(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts, List.of(), List.of());
    }

    public Md6Model withMeshes(List<Mesh> meshes) {
        return new Md6Model(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts, meshes, materials);
    }

    public Md6Model withMaterials(List<Material> materials) {
        return new Md6Model(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts, meshes, materials);
    }
}
