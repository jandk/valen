package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.game.eternal.reader.geometry.*;

import java.io.*;
import java.util.*;

public record StaticModel(
    StaticModelHeader header,
    List<StaticModelMeshInfo> meshInfos,
    StaticModelSettings settings,
    StaticModelGeoDecals geoDecals,
    List<Boolean> streamedLods,
    List<GeometryDiskLayout> streamDiskLayouts,
    List<Mesh> meshes,
    List<Material> materials
) {
    public static final int LodCount = 5;

    public static StaticModel read(DataSource source) throws IOException {
        var header = StaticModelHeader.read(source);
        var meshInfos = source.readStructs(header.numSurfaces(), StaticModelMeshInfo::read);
        var settings = StaticModelSettings.read(source);
        var geoDecals = StaticModelGeoDecals.read(source);
        var streamedLods = source.readStructs(header.numSurfaces() * LodCount, DataSource::readBoolByte);
        var layouts = header.streamable() ? readLayouts(source) : List.<GeometryDiskLayout>of();

        return new StaticModel(
            header,
            meshInfos,
            settings,
            geoDecals,
            streamedLods,
            layouts,
            List.of(),
            List.of()
        );
    }

    private static List<GeometryDiskLayout> readLayouts(DataSource source) throws IOException {
        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var lod = 0; lod < LodCount; lod++) {
            var memoryLayouts = source.readStructs(source.readInt(), GeometryMemoryLayout::read);
            layouts.add(GeometryDiskLayout.read(source, memoryLayouts));
        }
        return layouts;
    }

    public StaticModel withMeshes(List<Mesh> meshes) {
        return new StaticModel(
            header,
            meshInfos,
            settings,
            geoDecals,
            streamedLods,
            streamDiskLayouts,
            meshes,
            materials
        );
    }

    public StaticModel withMaterials(List<Material> materials) {
        return new StaticModel(
            header,
            meshInfos,
            settings,
            geoDecals,
            streamedLods,
            streamDiskLayouts,
            meshes,
            materials
        );
    }
}
