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

    public static StaticModel read(BinarySource source) throws IOException {
        var header = StaticModelHeader.read(source);
        var meshInfos = source.readObjects(header.numSurfaces(), StaticModelMeshInfo::read);
        var settings = StaticModelSettings.read(source);
        var geoDecals = StaticModelGeoDecals.read(source);
        var streamedLods = source.readObjects(header.numSurfaces() * LodCount, s -> s.readBool(BoolFormat.BYTE));
        var layouts = header.streamable() ? readLayouts(source) : List.<GeometryDiskLayout>of();

        return new StaticModel(
            header,
            meshInfos,
            settings,
            geoDecals,
            streamedLods,
            layouts
        );
    }

    private static List<GeometryDiskLayout> readLayouts(BinarySource source) throws IOException {
        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var lod = 0; lod < LodCount; lod++) {
            var memoryLayouts = source.readObjects(source.readInt(), GeometryMemoryLayout::read);
            layouts.add(GeometryDiskLayout.read(source, memoryLayouts));
        }
        return layouts;
    }
}
