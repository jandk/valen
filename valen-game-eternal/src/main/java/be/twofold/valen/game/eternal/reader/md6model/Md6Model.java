package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.reader.geometry.*;

import java.io.*;
import java.util.*;

public record Md6Model(
    Md6ModelHeader header,
    Md6ModelBoneInfo boneInfo,
    List<Md6ModelInfo> meshInfos,
    List<Md6ModelMaterialInfo> materialInfos,
    Md6ModelGeoDecals geoDecals,
    List<GeometryDiskLayout> layouts
) {
    public static Md6Model read(DataSource source) throws IOException {
        var header = Md6ModelHeader.read(source);
        var boneInfo = Md6ModelBoneInfo.read(source);
        var meshInfos = source.readObjects(source.readInt(), Md6ModelInfo::read);
        var materialInfos = source.readObjects(source.readInt(), Md6ModelMaterialInfo::read);
        var geoDecals = Md6ModelGeoDecals.read(source);
        var memoryLayouts = source.readObjects(source.readInt(), GeometryMemoryLayout::read);

        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var i = 0; i < 5; i++) {
            var subMemoryLayouts = List.copyOf(memoryLayouts.subList(i, i + 1));
            layouts.add(GeometryDiskLayout.read(source, subMemoryLayouts));
        }

        source.expectEnd();
        return new Md6Model(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts);
    }
}
