package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.geometry.*;

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

    public static StaticModel read(BetterBuffer buffer) {
        var header = StaticModelHeader.read(buffer);
        var meshInfos = buffer.getStructs(header.numMeshes(), StaticModelMeshInfo::read);
        var misc1 = StaticModelMisc1.read(buffer);
        var geoDecals = StaticModelGeoDecals.read(buffer);
        var misc2 = StaticModelMisc2.read(buffer);
        var streamedLods = buffer.getStructs(header.numMeshes() * LodCount, BetterBuffer::getByteAsBool);
        var layouts = header.streamed() ? readLayouts(buffer) : List.<GeometryDiskLayout>of();

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

    private static List<GeometryDiskLayout> readLayouts(BetterBuffer buffer) {
        var layouts = new ArrayList<GeometryDiskLayout>();
        for (var lod = 0; lod < LodCount; lod++) {
            var memoryLayouts = buffer.getStructs(buffer.getInt(), GeometryMemoryLayout::read);
            layouts.add(GeometryDiskLayout.read(buffer, memoryLayouts));
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
