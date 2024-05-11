package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.reader.geometry.*;

import java.io.*;
import java.util.*;

public record StaticModel(
    StaticModelHeader header,
    List<StaticModelMeshInfo> meshInfos,
    StaticModelMisc1 misc1,
    StaticModelGeoDecals geoDecals,
    StaticModelMisc2 misc2,
    List<Boolean> streamedLods,
    List<GeometryDiskLayout> streamDiskLayouts,
    List<Mesh> meshes,
    List<Material> materials
) {
    public static final int LodCount = 5;

    public static StaticModel read(DataSource source) throws IOException {
        var header = StaticModelHeader.read(source);
        var meshInfos = source.readStructs(header.numMeshes(), StaticModelMeshInfo::read);
        var misc1 = StaticModelMisc1.read(source);
        var geoDecals = StaticModelGeoDecals.read(source);
        var misc2 = StaticModelMisc2.read(source);
        var streamedLods = source.readStructs(header.numMeshes() * LodCount, DataSource::readBoolByte);
        var layouts = header.streamed() ? readLayouts(source) : List.<GeometryDiskLayout>of();

        return new StaticModel(
            header,
            meshInfos,
            misc1,
            geoDecals,
            misc2,
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
            misc1,
            geoDecals,
            misc2,
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
            misc1,
            geoDecals,
            misc2,
            streamedLods,
            streamDiskLayouts,
            meshes,
            materials
        );
    }
}
