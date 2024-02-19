package be.twofold.valen.reader.model;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.geometry.*;

import java.util.*;

public record Model(
    ModelHeader header,
    List<ModelMeshInfo> meshInfos,
    ModelMisc1 misc1,
    ModelGeoDecals geoDecals,
    ModelMisc2 misc2,
    List<Boolean> streamedLods,
    List<GeometryDiskLayout> streamDiskLayouts,
    List<Mesh> meshes,
    List<Material> materials
) {
    public static final int LodCount = 5;

    public static Model read(BetterBuffer buffer) {
        var header = ModelHeader.read(buffer);
        var meshInfos = buffer.getStructs(header.numMeshes(), ModelMeshInfo::read);
        var misc1 = ModelMisc1.read(buffer);
        var geoDecals = ModelGeoDecals.read(buffer);
        var misc2 = ModelMisc2.read(buffer);
        var streamedLods = buffer.getStructs(header.numMeshes() * LodCount, BetterBuffer::getByteAsBool);
        var layouts = header.streamed() ? readLayouts(buffer) : List.<GeometryDiskLayout>of();

        return new Model(
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

    public Model withMeshes(List<Mesh> meshes) {
        return new Model(
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

    public Model withMaterials(List<Material> materials) {
        return new Model(
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
