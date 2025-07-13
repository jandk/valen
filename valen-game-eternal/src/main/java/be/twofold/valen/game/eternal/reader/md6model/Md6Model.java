package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.BinaryReader;
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
    public static Md6Model read(BinaryReader reader) throws IOException {
        var header = Md6ModelHeader.read(reader);
        var boneInfo = Md6ModelBoneInfo.read(reader);
        var meshInfos = reader.readObjects(reader.readInt(), Md6ModelInfo::read);
        var materialInfos = reader.readObjects(reader.readInt(), Md6ModelMaterialInfo::read);
        var geoDecals = Md6ModelGeoDecals.read(reader);
        var memoryLayouts = reader.readObjects(reader.readInt(), GeometryMemoryLayout::read);

        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var i = 0; i < 5; i++) {
            var subMemoryLayouts = List.copyOf(memoryLayouts.subList(i, i + 1));
            layouts.add(GeometryDiskLayout.read(reader, subMemoryLayouts));
        }

        reader.expectEnd();
        return new Md6Model(header, boneInfo, meshInfos, materialInfos, geoDecals, layouts);
    }
}
