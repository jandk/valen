package be.twofold.valen.game.idtech.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class GeometryReader {
    public static Mesh readEmbeddedMesh(DataSource source, LodInfo lodInfo) throws IOException {
        var masks = GeometryVertexMask.FixedOrder.stream()
            .filter(mask -> (lodInfo.vertexMask() & mask.mask()) == mask.mask())
            .toList();

        var stride = masks.stream()
            .mapToInt(GeometryVertexMask::size)
            .sum();

        var offset = 0;
        var vertexAccessors = new ArrayList<GeoAccessor<?>>();
        for (var mask : masks) {
            var accessors = buildAccessors(offset, lodInfo.numVertices(), stride, mask, lodInfo, SkinningMode.None);
            vertexAccessors.addAll(accessors);
            offset += mask.size();
        }

        offset += stride * (lodInfo.numVertices() - 1);
        var faceInfo = new VertexBufferInfo<>(null, ComponentType.UNSIGNED_SHORT, 3);
        var faceAccessor = new GeoAccessor<>(offset, lodInfo.numFaces(), 6, faceInfo, Geometry.readFace());

        return new Geo(true).readMesh(source, faceAccessor, vertexAccessors);
    }

    public static List<Mesh> readStreamedMesh(
        DataSource source,
        List<LodInfo> lods,
        List<? extends GeoMemoryLayout> layouts,
        boolean animated
    ) throws IOException {
        var offsetsByLayout = layouts.stream().collect(Collectors.toUnmodifiableMap(
            GeoMemoryLayout::combinedVertexMask,
            layout -> new Offsets(
                layout.indexOffset(),
                Arrays.copyOf(layout.vertexOffsets(), layout.numVertexStreams())
            )));

        var meshes = new ArrayList<Mesh>();
        for (var lodInfo : lods) {
            for (var layout : layouts) {
                if (lodInfo.vertexMask() != layout.combinedVertexMask()) {
                    continue;
                }

                var offsets = offsetsByLayout.get(layout.combinedVertexMask());
                var vertexAccessors = new ArrayList<GeoAccessor<?>>();
                for (var v = 0; v < layout.numVertexStreams(); v++) {
                    var mask = GeometryVertexMask.from(layout.vertexMasks()[v]);
                    var skinningMode = animated ? SkinningMode.Fixed4 : SkinningMode.None;
                    vertexAccessors.addAll(buildAccessors(offsets.vertexOffsets[v], lodInfo.numVertices(), mask.size(), mask, lodInfo, skinningMode));
                    offsets.vertexOffsets[v] += lodInfo.numVertices() * mask.size();
                }

                var faceInfo = VertexBufferInfo.indices(ComponentType.UNSIGNED_SHORT);
                var faceAccessor = new GeoAccessor<>(offsets.indexOffset, lodInfo.numFaces(), 6, faceInfo, Geometry.readFace());
                offsets.indexOffset += lodInfo.numFaces() * 3 * Short.BYTES;

                meshes.add(new Geo(true).readMesh(source, faceAccessor, vertexAccessors));
            }
        }
        return meshes;
    }

    public static List<Mesh> readStreamedMesh(
        DataSource source,
        List<LodInfo> lods,
        boolean animated
    ) throws IOException {
        var offset = 0;
        var meshes = new ArrayList<Mesh>();
        for (LodInfo lod : lods) {
            int lodOffset = 0;
            var masks = GeometryVertexMask.fromMultiple(lod.vertexMask());
            var skinningMode = mapSkinningMode(masks, animated);
            System.out.println(skinningMode + "\t" + masks);
            var vertexAccessors = new ArrayList<GeoAccessor<?>>();
            for (var mask : masks) {
                int maskSize = mask.size();
                if (maskSize == 0) {
                    continue;
                }

                int aligner = (maskSize - lodOffset % maskSize) % maskSize;
                int bufferOffset = aligner + lodOffset;
                vertexAccessors.addAll(buildAccessors(offset + bufferOffset, lod.numVertices(), maskSize, mask, lod, skinningMode));
                lodOffset = lod.numVertices() * maskSize + bufferOffset;
            }

            var faceInfo = VertexBufferInfo.indices(ComponentType.UNSIGNED_SHORT);
            var faceAccessor = new GeoAccessor<>(offset + lodOffset, lod.numFaces(), 6, faceInfo, Geometry.readFace());
            lodOffset += lod.numFaces() * 3 * Short.BYTES;
            offset = (offset + lodOffset + 7) & ~7;

            meshes.add(new Geo(true).readMesh(source, faceAccessor, vertexAccessors));
        }

        return meshes;
    }

    private static SkinningMode mapSkinningMode(List<GeometryVertexMask> masks, boolean animated) {
        if (!animated) {
            return SkinningMode.None;
        }
        if (masks.contains(GeometryVertexMask.WGVS_SKINNING_1)) {
            return SkinningMode.Skinning1;
        }
        if (masks.contains(GeometryVertexMask.WGVS_SKINNING_6)) {
            return SkinningMode.Skinning6;
        }
        if (masks.contains(GeometryVertexMask.WGVS_SKINNING_8)) {
            return SkinningMode.Skinning8;
        }
        return SkinningMode.Fixed4;
    }

    private static List<GeoAccessor<?>> buildAccessors(int offset, int count, int stride, GeometryVertexMask mask, LodInfo lodInfo, SkinningMode skinningMode) {
        return switch (mask) {
            case WGVS_POSITION_SHORT -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.POSITION, Geometry.readPackedPosition(lodInfo.vertexScale(), lodInfo.vertexOffset()))
            );
            case WGVS_POSITION -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.POSITION, Geometry.readPosition(lodInfo.vertexScale(), lodInfo.vertexOffset()))
            );
            case WGVS_NORMAL_TANGENT -> {
                var normal = new GeoAccessor<>(offset, count, stride, VertexBufferInfo.NORMAL, Geometry.readPackedNormal());
                var tangent = new GeoAccessor<>(offset, count, stride, VertexBufferInfo.TANGENT, Geometry.readPackedTangent());
                var interleaved = switch (skinningMode) {
                    case None -> null;
                    case Fixed4 ->
                        new GeoAccessor<>(offset, count, stride, VertexBufferInfo.weights(ComponentType.FLOAT, 4), Geometry.readWeight4());
                    case Skinning1 ->
                        new GeoAccessor<>(offset, count, stride, VertexBufferInfo.joints(ComponentType.UNSIGNED_BYTE, 1), Geometry.readBone1());
                    case Skinning6 ->
                        new GeoAccessor<>(offset, count, stride, VertexBufferInfo.weights(ComponentType.FLOAT, 3), Geometry.readWeight6());
                    case Skinning8 ->
                        new GeoAccessor<>(offset, count, stride, VertexBufferInfo.weights(ComponentType.FLOAT, 3), Geometry.readWeight8());
                };
                yield interleaved != null ? List.of(normal, tangent, interleaved) : List.of(normal, tangent);
            }
            case WGVS_MATERIAL_UV, WGVS_MATERIAL_UV1, WGVS_LIGHTMAP_UV, WGVS_MATERIAL_UV2 -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.TEX_COORDS, Geometry.readUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_MATERIAL_UV_SHORT, WGVS_MATERIAL_UV1_SHORT, WGVS_LIGHTMAP_UV_SHORT, WGVS_MATERIAL_UV2_SHORT ->
                List.of(
                    new GeoAccessor<>(offset, count, stride, VertexBufferInfo.TEX_COORDS, Geometry.readPackedUV(lodInfo.uvScale(), lodInfo.uvOffset()))
                );
            case WGVS_COLOR -> {
                var info = switch (skinningMode) {
                    case Fixed4 -> VertexBufferInfo.joints(ComponentType.UNSIGNED_BYTE, 4);
                    default -> VertexBufferInfo.colors(ComponentType.UNSIGNED_BYTE);
                };

                yield List.of(
                    new GeoAccessor<>(offset, count, stride, info, Geometry.copyBytes(4))
                );
            }
            case WGVS_SKINNING_1 -> List.of();
            case WGVS_SKINNING_4 -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.joints(ComponentType.UNSIGNED_BYTE, 4), Geometry.copyBytes(4))
            );
            case WGVS_SKINNING_6 -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.joints(ComponentType.UNSIGNED_BYTE, 6), Geometry.copyBytes(6)),
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.weights(ComponentType.UNSIGNED_BYTE, 2), Geometry.copyBytes(2))
            );
            case WGVS_SKINNING_8 -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.joints(ComponentType.UNSIGNED_BYTE, 8), Geometry.copyBytes(8)),
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.weights(ComponentType.UNSIGNED_BYTE, 4), Geometry.copyBytes(4))
            );
        };
    }

    private static final class Offsets {
        private int indexOffset;
        private final int[] vertexOffsets;

        private Offsets(int indexOffset, int[] vertexOffsets) {
            this.indexOffset = indexOffset;
            this.vertexOffsets = vertexOffsets;
        }
    }
}
