package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.reader.geometry.*;

import java.io.*;
import java.util.*;

public record StaticModel(
    StaticModelHeader header,
    List<StaticModelMeshInfo> meshInfos,
    StaticModelSettings settings,
    StaticModelGeoDecals geoDecals,
    List<Boolean> streamedLods,
    List<GeometryDiskLayout> streamDiskLayouts
) {
    public static final int LodCount = 5;

    public static StaticModel read(BinaryReader reader) throws IOException {
        var header = StaticModelHeader.read(reader);
        var meshInfos = reader.readObjects(header.numSurfaces(), StaticModelMeshInfo::read);
        var settings = StaticModelSettings.read(reader);
        var geoDecals = StaticModelGeoDecals.read(reader);
        var streamedLods = reader.readObjects(header.numSurfaces() * LodCount, BinaryReader::readBoolByte);
        var layouts = header.streamable() ? readLayouts(reader) : List.<GeometryDiskLayout>of();

        return new StaticModel(
            header,
            meshInfos,
            settings,
            geoDecals,
            streamedLods,
            layouts
        );
    }

    private static List<GeometryDiskLayout> readLayouts(BinaryReader reader) throws IOException {
        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var lod = 0; lod < LodCount; lod++) {
            var memoryLayouts = reader.readObjects(reader.readInt(), GeometryMemoryLayout::read);
            layouts.add(GeometryDiskLayout.read(reader, memoryLayouts));
        }
        return layouts;
    }
}
