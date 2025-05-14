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
            var accessors = buildAccessors(offset, lodInfo.numVertices(), stride, mask, lodInfo, false);
            vertexAccessors.addAll(accessors);
            offset += mask.size();
        }

        offset += stride * (lodInfo.numVertices() - 1);
        var faceInfo = new VertexBufferInfo<>(null, ElementType.SCALAR, ComponentType.UNSIGNED_SHORT, false);
        var faceAccessor = new GeoAccessor<>(offset, lodInfo.numFaces() * 3, 2, faceInfo, Geometry.readFace());

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
                    vertexAccessors.addAll(buildAccessors(offsets.vertexOffsets[v], lodInfo.numVertices(), mask.size(), mask, lodInfo, animated));
                    offsets.vertexOffsets[v] += lodInfo.numVertices() * mask.size();
                }

                var faceInfo = VertexBufferInfo.indices(ComponentType.UNSIGNED_SHORT);
                var faceAccessor = new GeoAccessor<>(offsets.indexOffset, lodInfo.numFaces() * 3, 2, faceInfo, Geometry.readFace());
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
        for (var lodInfo : lods) {
            var masks = GeometryVertexMask.fromMultiple(lodInfo.vertexMask());

            var vertexAccessors = new ArrayList<GeoAccessor<?>>();
            for (var mask : masks) {
                vertexAccessors.addAll(buildAccessors(offset, lodInfo.numVertices(), mask.size(), mask, lodInfo, animated));
                offset += lodInfo.numVertices() * mask.size();
            }

            var faceInfo = VertexBufferInfo.indices(ComponentType.UNSIGNED_SHORT);
            var faceAccessor = new GeoAccessor<>(offset, lodInfo.numFaces() * 3, 2, faceInfo, Geometry.readFace());
            offset += lodInfo.numFaces() * 3 * Short.BYTES;
            offset = (offset + 7) & ~7;

            meshes.add(new Geo(true).readMesh(source, faceAccessor, vertexAccessors));
        }
        return meshes;
    }

    private static List<GeoAccessor<?>> buildAccessors(int offset, int count, int stride, GeometryVertexMask mask, LodInfo lodInfo, boolean animated) {
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
                var weights0 = new GeoAccessor<>(offset, count, stride, VertexBufferInfo.weights(0, ComponentType.UNSIGNED_BYTE), Geometry.readWeight());
                yield animated ? List.of(normal, tangent, weights0) : List.of(normal, tangent);
            }
            case WGVS_LIGHTMAP_UV_SHORT -> {
                throw new UnsupportedOperationException("WGVS_LIGHTMAP_UV_SHORT");
//                yield List.of(
//                    new GeoAccessor<>(offset, count, stride, VertexBufferInfo.texCoords(3), Geometry.readPackedUV(lodInfo.uvScale(), lodInfo.uvOffset()))
//                );
            }
            case WGVS_LIGHTMAP_UV -> {
                throw new UnsupportedOperationException("WGVS_LIGHTMAP_UV");
//                yield List.of(
//                    new GeoAccessor<>(offset, count, stride, VertexBufferInfo.texCoords(3), Geometry.readUV(lodInfo.uvScale(), lodInfo.uvOffset()))
//                );
            }
            case WGVS_MATERIAL_UV_SHORT -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.texCoords(0), Geometry.readPackedUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_MATERIAL_UV -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.texCoords(0), Geometry.readUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_MATERIAL_UV1 -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.texCoords(1), Geometry.readUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_MATERIAL_UV2 -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.texCoords(2), Geometry.readUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_MATERIAL_UV1_SHORT -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.texCoords(1), Geometry.readPackedUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_MATERIAL_UV2_SHORT -> List.of(
                new GeoAccessor<>(offset, count, stride, VertexBufferInfo.texCoords(2), Geometry.readPackedUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_COLOR -> {
                var info = animated
                    ? VertexBufferInfo.joints(0, ComponentType.UNSIGNED_BYTE)
                    : VertexBufferInfo.colors(0, ComponentType.UNSIGNED_BYTE);

                yield List.of(
                    new GeoAccessor<>(offset, count, stride, info, Geometry.readColor())
                );
            }
            case WGVS_SKINNING -> throw new UnsupportedOperationException("WGVS_SKINNING");
            case WGVS_SKINNING_1 -> List.of();
            case WGVS_SKINNING_4 -> throw new UnsupportedOperationException("WGVS_SKINNING_4");
            case WGVS_SKINNING_6 -> throw new UnsupportedOperationException("WGVS_SKINNING_6");
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
