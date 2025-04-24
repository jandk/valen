package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StaticModel(
    StaticModelHeader header,
    List<StaticModelMeshInfo> meshInfos,
    List<StaticModelTextureAxis> textureAxis,
    StaticModelGeoDecals geoDecals,
    List<Boolean> streamedLods,
    List<GeometryDiskLayout> streamDiskLayouts
) {
    public static final int LodCount = 5;

    public static StaticModel read(DataSource source, int version) throws IOException {
        var header = StaticModelHeader.read(source);
        var meshInfos = source.readObjects(header.numSurfaces(), source1 -> StaticModelMeshInfo.read(source1, version));
        if (version < 81) {
            source.readInts(3);
        }
        var textureAxis = source.readObjects(source.readInt(), s -> StaticModelTextureAxis.read(s, version));
        var geoDecals = StaticModelGeoDecals.read(source);
        var streamedLods = source.readObjects(header.numSurfaces() * header.numLods(), DataSource::readBoolByte);
        var layouts = header.streamable() ? readLayouts(source, version) : List.<GeometryDiskLayout>of();

        if (header.streamable()) {
            var vegetationDataPresent = source.readBoolInt();
            if (!vegetationDataPresent) {
                source.expectEnd();
            }
        }

        return new StaticModel(
            header,
            meshInfos,
            textureAxis,
            geoDecals,
            streamedLods,
            layouts
        );
    }

    private static List<GeometryDiskLayout> readLayouts(DataSource source, int version) throws IOException {
        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var lod = 0; lod < LodCount; lod++) {
            var memoryLayouts = source.readObjects(source.readInt(), GeometryMemoryLayout::read);
            layouts.add(GeometryDiskLayout.read(source, memoryLayouts, version));
        }
        return layouts;
    }
}
