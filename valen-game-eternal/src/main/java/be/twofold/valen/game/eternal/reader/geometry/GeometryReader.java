package be.twofold.valen.game.eternal.reader.geometry;

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
        var vertexAccessors = new ArrayList<Geo.Accessor<?>>();
        for (var mask : masks) {
            var accessors = buildAccessors(offset, lodInfo.numVertices(), stride, mask, lodInfo, false);
            vertexAccessors.addAll(accessors);
            offset += mask.size();
        }

        offset += stride * (lodInfo.numVertices() - 1);
        var faceInfo = new VertexBuffer.Info<>(null, ElementType.Scalar, ComponentType.UnsignedShort, false);
        var faceAccessor = new Geo.Accessor<>(offset, lodInfo.numFaces() * 3, 2, faceInfo, Geometry.readFace());

        return new Geo(true).readMesh(source, vertexAccessors, faceAccessor);
    }

    public static List<Mesh> readStreamedMesh(
        DataSource source,
        List<LodInfo> lods,
        List<GeometryMemoryLayout> layouts,
        boolean animated
    ) throws IOException {
        var offsetsByLayout = layouts.stream().collect(Collectors.toUnmodifiableMap(
            GeometryMemoryLayout::combinedVertexMask,
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
                var vertexAccessors = new ArrayList<Geo.Accessor<?>>();
                for (var v = 0; v < layout.numVertexStreams(); v++) {
                    var mask = GeometryVertexMask.from(layout.vertexMasks()[v]);
                    vertexAccessors.addAll(buildAccessors(offsets.vertexOffsets[v], lodInfo.numVertices(), mask.size(), mask, lodInfo, animated));
                    offsets.vertexOffsets[v] += lodInfo.numVertices() * mask.size();
                }

                var faceInfo = VertexBuffer.Info.indices(ComponentType.UnsignedShort);
                var faceAccessor = new Geo.Accessor<>(offsets.indexOffset, lodInfo.numFaces() * 3, 2, faceInfo, Geometry.readFace());
                offsets.indexOffset += lodInfo.numFaces() * 3 * Short.BYTES;

                meshes.add(new Geo(true).readMesh(source, vertexAccessors, faceAccessor));
            }
        }
        return meshes;
    }

    private static List<Geo.Accessor<?>> buildAccessors(int offset, int count, int stride, GeometryVertexMask mask, LodInfo lodInfo, boolean animated) {
        return switch (mask) {
            case WGVS_POSITION_SHORT -> List.of(
                new Geo.Accessor<>(offset, count, stride, VertexBuffer.Info.POSITION, Geometry.readPackedPosition(lodInfo.vertexScale(), lodInfo.vertexOffset()))
            );
            case WGVS_POSITION -> List.of(
                new Geo.Accessor<>(offset, count, stride, VertexBuffer.Info.POSITION, Geometry.readPosition(lodInfo.vertexScale(), lodInfo.vertexOffset()))
            );
            case WGVS_NORMAL_TANGENT -> {
                var normal = new Geo.Accessor<>(offset, count, stride, VertexBuffer.Info.NORMAL, Geometry.readPackedNormal());
                var tangent = new Geo.Accessor<>(offset, count, stride, VertexBuffer.Info.TANGENT, Geometry.readPackedTangent());
                var weights0 = new Geo.Accessor<>(offset, count, stride, VertexBuffer.Info.weights(0, ComponentType.UnsignedByte), Geometry.readWeight());
                yield animated ? List.of(normal, tangent, weights0) : List.of(normal, tangent);
            }
            case WGVS_LIGHTMAP_UV_SHORT -> List.of(
                new Geo.Accessor<>(offset, count, stride, VertexBuffer.Info.texCoords(1), Geometry.readPackedUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_LIGHTMAP_UV -> List.of(
                new Geo.Accessor<>(offset, count, stride, VertexBuffer.Info.texCoords(1), Geometry.readUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_MATERIAL_UV_SHORT -> List.of(
                new Geo.Accessor<>(offset, count, stride, VertexBuffer.Info.texCoords(0), Geometry.readPackedUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_MATERIAL_UV -> List.of(
                new Geo.Accessor<>(offset, count, stride, VertexBuffer.Info.texCoords(0), Geometry.readUV(lodInfo.uvScale(), lodInfo.uvOffset()))
            );
            case WGVS_COLOR -> {
                var info = animated
                    ? VertexBuffer.Info.joints(0, ComponentType.UnsignedByte)
                    : VertexBuffer.Info.colors(0, ComponentType.UnsignedByte);

                yield List.of(
                    new Geo.Accessor<>(offset, count, stride, info, Geometry.readColor())
                );
            }
            case WGVS_MATERIALS -> List.of();
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
