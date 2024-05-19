package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public final class GeometryReader {
    public static Mesh readEmbeddedMesh(DataSource source, LodInfo lodInfo) throws IOException {
        var masks = GeometryVertexMask.FixedOrder.stream()
            .filter(mask -> (lodInfo.vertexMask() & mask.mask()) == mask.mask())
            .toList();

        var stride = masks.stream()
            .mapToInt(GeometryVertexMask::size)
            .sum();

        var offset = 0;
        var accessors = new ArrayList<Geo.Accessor>();

        for (var mask : masks) {
            for (var info : buildAccessor(mask, false)) {
                var reader = reader(mask, info.semantic(), lodInfo);
                var accessor = new Geo.Accessor(offset, lodInfo.numVertices(), stride, info, reader);
                accessors.add(accessor);
            }
            offset += mask.size();
        }

        offset += stride * (lodInfo.numVertices() - 1);
        var faceInfo = new VertexBuffer.Info(null, ElementType.Scalar, ComponentType.UnsignedShort, false);
        var faceAccessor = new Geo.Accessor(offset, lodInfo.numFaces() * 3, 2, faceInfo, Geometry.readFace());

        return Geo.readMesh(source, accessors, faceAccessor);
    }

    public static List<Mesh> readStreamedMesh(
        DataSource source,
        List<LodInfo> lods,
        List<GeometryMemoryLayout> layouts,
        boolean animated
    ) throws IOException {
        var meshes = new ArrayList<Mesh>();
        for (var layout : layouts) {
            var offsets = Arrays.copyOf(layout.vertexOffsets(), layout.numVertexStreams());

            for (var lodInfo : lods) {
                if (lodInfo.vertexMask() != layout.combinedVertexMask()) {
                    continue;
                }

                var vertexAccessors = new ArrayList<Geo.Accessor>();
                for (var v = 0; v < layout.numVertexStreams(); v++) {
                    var mask = GeometryVertexMask.from(layout.vertexMasks()[v]);
                    for (var info : buildAccessor(mask, animated)) {
                        var reader = reader(mask, info.semantic(), lodInfo);
                        var accessor = new Geo.Accessor(offsets[v], lodInfo.numVertices(), mask.size(), info, reader);
                        vertexAccessors.add(accessor);
                    }
                    offsets[v] += mask.size() * lodInfo.numVertices();
                }

                var faceInfo = new VertexBuffer.Info(null, ElementType.Scalar, ComponentType.UnsignedShort, false);
                var faceAccessor = new Geo.Accessor(layout.indexOffset(), lodInfo.numFaces() * 3, 2, faceInfo, Geometry.readFace());

                meshes.add(Geo.readMesh(source, vertexAccessors, faceAccessor));
            }
        }
        return meshes;
    }

    private static List<VertexBuffer.Info> buildAccessor(GeometryVertexMask mask, boolean animated) {
        return switch (mask) {
            case WGVS_POSITION_SHORT, WGVS_POSITION -> List.of(
                new VertexBuffer.Info(Semantic.Position, ElementType.Vector3, ComponentType.Float, false)
            );
            case WGVS_NORMAL_TANGENT -> List.of(
                new VertexBuffer.Info(Semantic.Normal, ElementType.Vector3, ComponentType.Float, false),
                new VertexBuffer.Info(Semantic.Tangent, ElementType.Vector4, ComponentType.Float, false)
            );
            case WGVS_LIGHTMAP_UV_SHORT, WGVS_LIGHTMAP_UV -> List.of(
                new VertexBuffer.Info(Semantic.TexCoord1, ElementType.Vector2, ComponentType.Float, false)
            );
            case WGVS_MATERIAL_UV_SHORT, WGVS_MATERIAL_UV -> List.of(
                new VertexBuffer.Info(Semantic.TexCoord0, ElementType.Vector2, ComponentType.Float, false)
            );
            case WGVS_COLOR -> List.of(
                new VertexBuffer.Info(animated ? Semantic.Joints0 : Semantic.Color0, ElementType.Vector4, ComponentType.UnsignedByte, true)
            );
            case WGVS_MATERIALS -> List.of();
        };
    }

    private static Geo.Reader reader(GeometryVertexMask mask, Semantic semantic, LodInfo lodInfo) {
        return switch (mask) {
            case WGVS_POSITION_SHORT -> Geometry.readPackedPosition(lodInfo.vertexOffset(), lodInfo.vertexScale());
            case WGVS_POSITION -> Geometry.readPosition(lodInfo.vertexOffset(), lodInfo.vertexScale());
            case WGVS_NORMAL_TANGENT -> switch (semantic) {
                case Semantic.Normal() -> Geometry.readPackedNormal();
                case Semantic.Tangent() -> Geometry.readPackedTangent();
                case Semantic.Weights(var ignored) -> Geometry.readWeight();
                default -> throw new IllegalStateException("Unexpected value: " + semantic);
            };
            case WGVS_LIGHTMAP_UV_SHORT, WGVS_MATERIAL_UV_SHORT -> Geometry.readPackedUV(lodInfo.uvOffset(), lodInfo.uvScale());
            case WGVS_LIGHTMAP_UV, WGVS_MATERIAL_UV -> Geometry.readUV(lodInfo.uvOffset(), lodInfo.uvScale());
            case WGVS_COLOR -> Geometry.readColor();
            case WGVS_MATERIALS -> throw new UnsupportedOperationException();
        };
    }
}
