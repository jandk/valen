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

    public static StaticModel read(BinaryReader reader, int version) throws IOException {
        var header = StaticModelHeader.read(reader);
        var meshInfos = reader.readObjects(header.numSurfaces(), source1 -> StaticModelMeshInfo.read(source1, version));
        if (version < 81) {
            reader.readInts(3);
        }
        var textureAxis = reader.readObjects(reader.readInt(), s -> StaticModelTextureAxis.read(s, version));
        var geoDecals = StaticModelGeoDecals.read(reader);
        var streamedLods = reader.readObjects(header.numSurfaces() * header.numLods(), BinaryReader::readBoolByte);
        var layouts = header.streamable() ? readLayouts(reader, version) : List.<GeometryDiskLayout>of();

        if (header.streamable()) {
            var vegetationDataPresent = reader.readBoolInt();
            if (!vegetationDataPresent) {
                reader.expectEnd();
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

    private static List<GeometryDiskLayout> readLayouts(BinaryReader reader, int version) throws IOException {
        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var lod = 0; lod < LodCount; lod++) {
            var memoryLayouts = reader.readObjects(reader.readInt(), GeometryMemoryLayout::read);
            layouts.add(GeometryDiskLayout.read(reader, memoryLayouts, version));
        }
        return layouts;
    }
}
