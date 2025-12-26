package be.twofold.valen.game.idtech.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;

import java.util.*;
import java.util.stream.*;

public final class GeometryReader {
    public static Mesh readEmbeddedMesh(BinarySource source, LodInfo lodInfo) {
        var masks = GeometryVertexMask.FixedOrder.stream()
            .filter(mask -> (lodInfo.vertexMask() & mask.mask()) == mask.mask())
            .toList();

        var stride = masks.stream()
            .mapToInt(GeometryVertexMask::size)
            .sum();

        var offset = 0;
        var builder = GeoMeshInfo.builder(lodInfo.numFaces() * 3, lodInfo.numVertices());
        for (var mask : masks) {
            buildAccessors(offset, stride, mask, lodInfo, SkinningMode.None, builder);
            offset += mask.size();
        }

        offset += stride * (lodInfo.numVertices() - 1);
        builder.indices(offset, Short.BYTES, GeoReader.readShortAsInt());

        return new Geo(true).readMesh(source, builder.build());
    }

    public static List<Mesh> readStreamedMesh(
            BinarySource source,
        List<LodInfo> lods,
        List<? extends GeoMemoryLayout> layouts,
        boolean animated
    ) {
        var offsetsByLayout = layouts.stream().collect(Collectors.toUnmodifiableMap(
            GeoMemoryLayout::combinedVertexMask,
            layout -> new Offsets(
                layout.indexOffset(),
                layout.vertexOffsets().stream().limit(layout.numVertexStreams()).toArray()
            )));

        var meshes = new ArrayList<Mesh>();
        for (var lodInfo : lods) {
            for (var layout : layouts) {
                if (lodInfo.vertexMask() != layout.combinedVertexMask()) {
                    continue;
                }

                var offsets = offsetsByLayout.get(layout.combinedVertexMask());
                var builder = GeoMeshInfo.builder(lodInfo.numFaces() * 3, lodInfo.numVertices());
                for (var v = 0; v < layout.numVertexStreams(); v++) {
                    var mask = GeometryVertexMask.from(layout.vertexMasks().get(v));
                    var skinningMode = animated ? SkinningMode.Fixed4 : SkinningMode.None;
                    buildAccessors(offsets.vertexOffsets[v], mask.size(), mask, lodInfo, skinningMode, builder);
                    offsets.vertexOffsets[v] += lodInfo.numVertices() * mask.size();
                }

                builder.indices(offsets.indexOffset, Short.BYTES, GeoReader.readShortAsInt());
                offsets.indexOffset += lodInfo.numFaces() * 3 * Short.BYTES;

                meshes.add(new Geo(true).readMesh(source, builder.build()));
            }
        }
        return meshes;
    }

    public static List<Mesh> readStreamedMesh(
            BinarySource source,
        List<LodInfo> lods,
        boolean animated
    ) {
        var offset = 0;
        var meshes = new ArrayList<Mesh>();
        for (LodInfo lod : lods) {
            int lodOffset = 0;
            var masks = GeometryVertexMask.fromMultiple(lod.vertexMask());
            var skinningMode = mapSkinningMode(masks, animated);
            var builder = GeoMeshInfo.builder(lod.numFaces() * 3, lod.numVertices());
            for (var mask : masks) {
                int maskSize = mask.size();
                int aligner = maskSize == 0 ? 0 : (maskSize - lodOffset % maskSize) % maskSize;
                int bufferOffset = aligner + lodOffset;
                buildAccessors(offset + bufferOffset, maskSize, mask, lod, skinningMode, builder);
                lodOffset = lod.numVertices() * maskSize + bufferOffset;
            }

            builder.indices(offset + lodOffset, Short.BYTES, GeoReader.readShortAsInt());
            lodOffset += lod.numFaces() * 3 * Short.BYTES;
            offset = (offset + lodOffset + 7) & ~7;

            meshes.add(new Geo(true)
                    .readMesh(source, builder.build()));
        }

        return meshes;
    }

    private static SkinningMode mapSkinningMode(List<GeometryVertexMask> masks, boolean animated) {
        if (!animated) {
            return SkinningMode.None;
        } else if (masks.contains(GeometryVertexMask.SKINNING_1)) {
            return SkinningMode.Skinning1;
        } else if (masks.contains(GeometryVertexMask.SKINNING_4)) {
            return SkinningMode.Skinning4;
        } else if (masks.contains(GeometryVertexMask.SKINNING_6)) {
            return SkinningMode.Skinning6;
        } else if (masks.contains(GeometryVertexMask.SKINNING_8)) {
            return SkinningMode.Skinning8;
        } else {
            return SkinningMode.Fixed4;
        }
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private static GeoMeshInfo.Builder buildAccessors(int offset, int stride, GeometryVertexMask mask, LodInfo lodInfo, SkinningMode skinningMode, GeoMeshInfo.Builder builder) {
        return switch (mask) {
            case POSITION_SHORT ->
                builder.positions(offset, stride, GeoReader.readPackedPosition(lodInfo.vertexScale(), lodInfo.vertexOffset()));
            case POSITION ->
                builder.positions(offset, stride, GeoReader.readPosition(lodInfo.vertexScale(), lodInfo.vertexOffset()));
            case NORMAL_TANGENT -> {
                builder
                    .normals(offset, stride, GeoReader.readPackedNormal())
                    .tangents(offset, stride, GeoReader.readPackedTangent());
                yield switch (skinningMode) {
                    case None -> builder;
                    case Fixed4, Skinning4 -> builder.weights(offset, stride, 4, GeoReader.readWeight4());
                    case Skinning1 -> builder.joints(offset, stride, 1, GeoReader.readBone1());
                    case Skinning6 -> builder.weights(offset, stride, 4, GeoReader.readWeight6());
                    case Skinning8 -> builder.weights(offset, stride, 4, GeoReader.readWeight8());
                };
            }
            case MATERIAL_UV, MATERIAL_UV1, LIGHTMAP_UV, MATERIAL_UV2 ->
                builder.addTexCoords(offset, stride, GeoReader.readUV(lodInfo.uvScale(), lodInfo.uvOffset()));
            case MATERIAL_UV_SHORT, MATERIAL_UV1_SHORT, LIGHTMAP_UV_SHORT, MATERIAL_UV2_SHORT ->
                builder.addTexCoords(offset, stride, GeoReader.readPackedUV(lodInfo.uvScale(), lodInfo.uvOffset()));
            case COLOR -> switch (skinningMode) {
                case Fixed4 -> builder.joints(offset, stride, 4, GeoReader.copyBytesAsShorts(4));
                default -> builder.addColors(offset, stride, GeoReader.copyBytes(4));
            };
            case SKINNING_1 -> switch (skinningMode) {
                case None -> builder;
                default -> builder.weights(offset, stride, 1, (_, dst, offset0) -> dst.set(offset0, 1.0f));
            };
            case SKINNING_4 -> builder.joints(offset, stride, 4, GeoReader.copyBytesAsShorts(4));
            case SKINNING_6 -> builder
                .joints(offset, stride, 6, GeoReader.copyBytesAsShorts(6))
                .custom("W", offset + 6, stride, 2, GeoReader.copyBytesAsFloats(2), ComponentType.FLOAT, ElementType.SCALAR);
            case SKINNING_8 -> builder
                .joints(offset, stride, 8, GeoReader.copyBytesAsShorts(8))
                .custom("W", offset + 8, stride, 4, GeoReader.copyBytesAsFloats(2), ComponentType.FLOAT, ElementType.SCALAR);
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
