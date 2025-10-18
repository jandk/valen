package be.twofold.valen.game.idtech.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class GeometryReader {
    public static Mesh readEmbeddedMesh(BinaryReader reader, LodInfo lodInfo) throws IOException {
        var masks = GeometryVertexMask.FixedOrder.stream()
            .filter(mask -> (lodInfo.vertexMask() & mask.mask()) == mask.mask())
            .toList();

        var stride = masks.stream()
            .mapToInt(GeometryVertexMask::size)
            .sum();

        var offset = 0;
        var vertexAccessors = new ArrayList<GeoAccessor<?>>();
        for (var mask : masks) {
            var accessors = buildAccessors(offset, stride, mask, lodInfo, SkinningMode.None);
            vertexAccessors.addAll(accessors);
            offset += mask.size();
        }

        offset += stride * (lodInfo.numVertices() - 1);
        var faceInfo = new VertexBufferInfo<>(null, ComponentType.UNSIGNED_INT, 3);
        var faceAccessor = new GeoAccessor<>(offset, Short.BYTES, faceInfo, GeoReader.readShortAsInt());

        return new Geo(true)
            .readMesh(reader, faceAccessor, lodInfo.numFaces() * 3, vertexAccessors, lodInfo.numVertices());
    }

    public static List<Mesh> readStreamedMesh(
        BinaryReader reader,
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
                    vertexAccessors.addAll(buildAccessors(offsets.vertexOffsets[v], mask.size(), mask, lodInfo, skinningMode));
                    offsets.vertexOffsets[v] += lodInfo.numVertices() * mask.size();
                }

                var faceInfo = VertexBufferInfo.indices(ComponentType.UNSIGNED_INT);
                var faceAccessor = new GeoAccessor<>(offsets.indexOffset, Short.BYTES, faceInfo, GeoReader.readShortAsInt());
                offsets.indexOffset += lodInfo.numFaces() * 3 * Short.BYTES;

                meshes.add(new Geo(true)
                    .readMesh(reader, faceAccessor, lodInfo.numFaces() * 3, vertexAccessors, lodInfo.numVertices()));
            }
        }
        return meshes;
    }

    public static List<Mesh> readStreamedMesh(
        BinaryReader reader,
        List<LodInfo> lods,
        boolean animated
    ) throws IOException {
        var offset = 0;
        var meshes = new ArrayList<Mesh>();
        for (LodInfo lod : lods) {
            int lodOffset = 0;
            var masks = GeometryVertexMask.fromMultiple(lod.vertexMask());
            var skinningMode = mapSkinningMode(masks, animated);
            var vertexAccessors = new ArrayList<GeoAccessor<?>>();
            for (var mask : masks) {
                int maskSize = mask.size();
                int aligner = maskSize == 0 ? 0 : (maskSize - lodOffset % maskSize) % maskSize;
                int bufferOffset = aligner + lodOffset;
                vertexAccessors.addAll(buildAccessors(offset + bufferOffset, maskSize, mask, lod, skinningMode));
                lodOffset = lod.numVertices() * maskSize + bufferOffset;
            }

            var faceInfo = VertexBufferInfo.indices(ComponentType.UNSIGNED_INT);
            var faceAccessor = new GeoAccessor<>(offset + lodOffset, Short.BYTES, faceInfo, GeoReader.readShortAsInt());
            lodOffset += lod.numFaces() * 3 * Short.BYTES;
            offset = (offset + lodOffset + 7) & ~7;

            meshes.add(new Geo(true)
                .readMesh(reader, faceAccessor, lod.numFaces() * 3, vertexAccessors, lod.numVertices()));
        }

        return meshes;
    }

    private static SkinningMode mapSkinningMode(List<GeometryVertexMask> masks, boolean animated) {
        if (!animated) {
            return SkinningMode.None;
        }
        if (masks.contains(GeometryVertexMask.SKINNING_1)) {
            return SkinningMode.Skinning1;
        }
        if (masks.contains(GeometryVertexMask.SKINNING_4)) {
            return SkinningMode.Skinning4;
        }
        if (masks.contains(GeometryVertexMask.SKINNING_6)) {
            return SkinningMode.Skinning6;
        }
        if (masks.contains(GeometryVertexMask.SKINNING_8)) {
            return SkinningMode.Skinning8;
        }
        return SkinningMode.Fixed4;
    }

    private static List<GeoAccessor<?>> buildAccessors(int offset, int stride, GeometryVertexMask mask, LodInfo lodInfo, SkinningMode skinningMode) {
        return switch (mask) {
            case POSITION_SHORT -> List.of(
                new GeoAccessor<>(offset, stride, VertexBufferInfo.POSITION, GeoReader.readPackedPosition(lodInfo.vertexScale(), lodInfo.vertexOffset()))
            );
            case POSITION -> List.of(
                new GeoAccessor<>(offset, stride, VertexBufferInfo.POSITION, GeoReader.readPosition(lodInfo.vertexScale(), lodInfo.vertexOffset()))
            );
            case NORMAL_TANGENT -> {
                var normal = new GeoAccessor<>(offset, stride, VertexBufferInfo.NORMAL, GeoReader.readPackedNormal());
                var tangent = new GeoAccessor<>(offset, stride, VertexBufferInfo.TANGENT, GeoReader.readPackedTangent());
                var interleaved = switch (skinningMode) {
                    case None -> null;
                    case Fixed4 ->
                        new GeoAccessor<>(offset, stride, VertexBufferInfo.weights(ComponentType.FLOAT, 4), GeoReader.readWeight4(true));
                    case Skinning4 ->
                        new GeoAccessor<>(offset, stride, VertexBufferInfo.weights(ComponentType.FLOAT, 3), GeoReader.readWeight4(false));
                    case Skinning1 ->
                        new GeoAccessor<>(offset, stride, VertexBufferInfo.joints(ComponentType.UNSIGNED_SHORT, 1), GeoReader.readBone1());
                    case Skinning6 ->
                        new GeoAccessor<>(offset, stride, VertexBufferInfo.weights(ComponentType.FLOAT, 3), GeoReader.readWeight6());
                    case Skinning8 ->
                        new GeoAccessor<>(offset, stride, VertexBufferInfo.weights(ComponentType.FLOAT, 3), GeoReader.readWeight8());
                };
                yield interleaved != null ? List.of(normal, tangent, interleaved) : List.of(normal, tangent);
            }
            case MATERIAL_UV, MATERIAL_UV1, LIGHTMAP_UV, MATERIAL_UV2 -> List.of(
                new GeoAccessor<>(offset, stride, VertexBufferInfo.TEX_COORDS, GeoReader.readUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case MATERIAL_UV_SHORT, MATERIAL_UV1_SHORT, LIGHTMAP_UV_SHORT, MATERIAL_UV2_SHORT -> List.of(
                new GeoAccessor<>(offset, stride, VertexBufferInfo.TEX_COORDS, GeoReader.readPackedUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case COLOR -> List.of(
                switch (skinningMode) {
                    case Fixed4 ->
                        new GeoAccessor<>(offset, stride, VertexBufferInfo.joints(ComponentType.UNSIGNED_SHORT, 4), GeoReader.copyBytesAsShorts(4));
                    default ->
                        new GeoAccessor<>(offset, stride, VertexBufferInfo.colors(ComponentType.UNSIGNED_BYTE), GeoReader.copyBytes(4));
                }
            );
            case SKINNING_1 -> skinningMode != SkinningMode.None ? List.of(
                new GeoAccessor<>(offset, stride, VertexBufferInfo.weights(ComponentType.FLOAT, 1), (_, dst, offset0) -> {
                    dst.setFloat(offset0, 1.0f);
                    return 1;
                })
            ) : List.of();
            case SKINNING_4 -> List.of(
                new GeoAccessor<>(offset, stride, VertexBufferInfo.joints(ComponentType.UNSIGNED_SHORT, 4), GeoReader.copyBytesAsShorts(4))
            );
            case SKINNING_6 -> List.of(
                new GeoAccessor<>(offset, stride, VertexBufferInfo.joints(ComponentType.UNSIGNED_SHORT, 6), GeoReader.copyBytesAsShorts(6)),
                new GeoAccessor<>(offset + 6, stride, VertexBufferInfo.weights(ComponentType.FLOAT, 2), GeoReader.copyBytesAsFloats(2))
            );
            case SKINNING_8 -> List.of(
                new GeoAccessor<>(offset, stride, VertexBufferInfo.joints(ComponentType.UNSIGNED_SHORT, 8), GeoReader.copyBytesAsShorts(8)),
                new GeoAccessor<>(offset + 8, stride, VertexBufferInfo.weights(ComponentType.FLOAT, 4), GeoReader.copyBytesAsFloats(4))
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
