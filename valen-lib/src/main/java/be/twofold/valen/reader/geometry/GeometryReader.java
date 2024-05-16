package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;

public final class GeometryReader {
    private final boolean hasWeights;
    private final List<FloatBuffer> positionBuffers = new ArrayList<>();
    private final List<FloatBuffer> normalBuffers = new ArrayList<>();
    private final List<FloatBuffer> tangentBuffers = new ArrayList<>();
    private final List<FloatBuffer> texCoord0Buffers = new ArrayList<>();
    private final List<FloatBuffer> texCoord1Buffers = new ArrayList<>();
    private final List<ByteBuffer> colorBuffers = new ArrayList<>();
    private final List<ByteBuffer> weightBuffers = new ArrayList<>();
    private final List<ShortBuffer> indexBuffers = new ArrayList<>();

    public GeometryReader(boolean hasWeights) {
        this.hasWeights = hasWeights;
    }

    public List<Mesh> readStreamedMeshes(DataSource source, List<LodInfo> lods, List<GeometryMemoryLayout> layouts) throws IOException {
        for (var layout : layouts) {
            for (int i = 0; i < layout.numVertexStreams(); i++) {
                var mask = layout.vertexMasks()[i];
                var offset = layout.vertexOffsets()[i];

                source.seek(offset);
                for (LodInfo lod : lods) {
                    if (lod.flags() == layout.combinedVertexMask()) {
                        readVertexBuffer(source, lod, GeometryVertexMask.from(mask));
                    }
                }
            }
        }

        for (var layout : layouts) {
            source.seek(layout.indexOffset());
            for (LodInfo lod : lods) {
                if (lod.flags() == layout.combinedVertexMask()) {
                    indexBuffers.add(readIndexBuffer(source, lod));
                }
            }
        }

        return IntStream.range(0, lods.size())
            .mapToObj(this::getMesh)
            .toList();
    }

    public Mesh readMergedMesh(DataSource source, LodInfo lodInfo) throws IOException {
        var masks = Arrays.stream(GeometryVertexMask.values())
            .filter(mask -> (lodInfo.flags() & mask.mask()) == mask.mask())
            .toList();

        var positions = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        var texCoords = lodInfo.flags() != 0x0801d ? FloatBuffer.allocate(lodInfo.numVertices() * 2) : null;
        var normals = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        var tangents = FloatBuffer.allocate(lodInfo.numVertices() * 4);
        var indices = ShortBuffer.allocate(lodInfo.numFaces() * 3);

        for (var i = 0; i < lodInfo.numVertices(); i++) {
            Geometry.readPosition(source, positions, lodInfo.vertexOffset(), lodInfo.vertexScale());
            if (lodInfo.flags() != 0x0801d) {
                Geometry.readUV(source, texCoords, lodInfo.uvOffset(), lodInfo.uvScale());
            }

            Geometry.readPackedNormal(source, normals);
            source.skip(-8);
            Geometry.readPackedTangent(source, tangents);
            source.expectInt(-1);

            source.skip(8); // skip lightmap UVs

            if (lodInfo.flags() == 0x1801f) {
                source.expectInt(-1);
                source.expectInt(0);
            }
        }

        for (var i = 0; i < lodInfo.numFaces(); i++) {
            indices.put(source.readShort());
            indices.put(source.readShort());
            indices.put(source.readShort());
        }

        var faceBuffer = new VertexBuffer(indices.flip(), ElementType.Scalar, ComponentType.UnsignedShort, false);
        var vertexBuffers = new HashMap<Semantic, VertexBuffer>();
        vertexBuffers.put(Semantic.Position, new VertexBuffer(positions.flip(), ElementType.Vector3, ComponentType.Float, false));
        vertexBuffers.put(Semantic.Normal, new VertexBuffer(normals.flip(), ElementType.Vector3, ComponentType.Float, false));
        vertexBuffers.put(Semantic.Tangent, new VertexBuffer(tangents.flip(), ElementType.Vector4, ComponentType.Float, false));
        if (texCoords != null) {
            vertexBuffers.put(Semantic.TexCoord0, new VertexBuffer(texCoords.flip(), ElementType.Vector2, ComponentType.Float, false));
        }
        return new Mesh(faceBuffer, vertexBuffers, -1);
    }

    private void readVertexBuffer(DataSource source, LodInfo lod, GeometryVertexMask mask) throws IOException {
        switch (mask) {
            case WGVS_POSITION_SHORT -> positionBuffers.add(readPackedVertices(source, lod));
            case WGVS_POSITION -> positionBuffers.add(readVertices(source, lod));
            case WGVS_NORMAL_TANGENT -> {
                // TODO: Merge this reading
                long start = source.tell();
                normalBuffers.add(readPackedNormals(source, lod));
                source.seek(start);
                tangentBuffers.add(readPackedTangents(source, lod));
                source.seek(start);
                weightBuffers.add(readWeights(source, lod));
            }
            case WGVS_LIGHTMAP_UV_SHORT -> texCoord1Buffers.add(readPackedUVs(source, lod));
            case WGVS_LIGHTMAP_UV -> texCoord1Buffers.add(readUVs(source, lod));
            case WGVS_MATERIAL_UV_SHORT -> texCoord0Buffers.add(readPackedUVs(source, lod));
            case WGVS_MATERIAL_UV -> texCoord0Buffers.add(readUVs(source, lod));
            case WGVS_COLOR -> colorBuffers.add(readColors(source, lod));
            // case WGVS_MATERIALS -> null;
            default -> throw new RuntimeException("Unknown mask: " + mask);
        }
    }

    public FloatBuffer readVertices(DataSource source, LodInfo lod) throws IOException {
        var dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPosition(source, dst, lod.vertexOffset(), lod.vertexScale());
        }
        return dst.flip();
    }

    public FloatBuffer readPackedVertices(DataSource source, LodInfo lod) throws IOException {
        var dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedPosition(source, dst, lod.vertexOffset(), lod.vertexScale());
        }
        return dst.flip();
    }

    public FloatBuffer readPackedNormals(DataSource source, LodInfo lod) throws IOException {
        var dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedNormal(source, dst);
        }
        return dst.flip();
    }

    public FloatBuffer readPackedTangents(DataSource source, LodInfo lod) throws IOException {
        var dst = FloatBuffer.allocate(lod.numVertices() * 4);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedTangent(source, dst);
        }
        return dst.flip();
    }

    public ByteBuffer readWeights(DataSource source, LodInfo lod) throws IOException {
        var dst = ByteBuffer.allocate(lod.numVertices() * 4);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readWeight(source, dst);
        }
        return dst.flip();
    }

    public FloatBuffer readUVs(DataSource source, LodInfo lod) throws IOException {
        var dst = FloatBuffer.allocate(lod.numVertices() * 2);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readUV(source, dst, lod.uvOffset(), lod.uvScale());
        }
        return dst.flip();
    }

    public FloatBuffer readPackedUVs(DataSource source, LodInfo lod) throws IOException {
        var dst = FloatBuffer.allocate(lod.numVertices() * 2);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedUV(source, dst, lod.uvOffset(), lod.uvScale());
        }
        return dst.flip();
    }

    public ByteBuffer readColors(DataSource source, LodInfo lod) throws IOException {
        var dst = ByteBuffer.allocate(lod.numVertices() * 4);
        dst.put(source.readBytes(lod.numVertices() * 4));
        return dst.flip();
    }

    public ShortBuffer readIndexBuffer(DataSource source, LodInfo lod) throws IOException {
        var dst = ShortBuffer.allocate(lod.numFaces() * 3);
        for (var i = 0; i < lod.numFaces(); i++) {
            var f1 = source.readShort();
            var f2 = source.readShort();
            var f3 = source.readShort();

            dst.put(f3);
            dst.put(f2);
            dst.put(f1);
        }
        return dst.flip();
    }

    private Mesh getMesh(int i) {
        var faceBuffer = new VertexBuffer(indexBuffers.get(i), ElementType.Scalar, ComponentType.UnsignedShort, false);
        var vertexBuffers = new HashMap<Semantic, VertexBuffer>();
        vertexBuffers.put(Semantic.Position, new VertexBuffer(positionBuffers.get(i), ElementType.Vector3, ComponentType.Float, false));
        vertexBuffers.put(Semantic.Normal, new VertexBuffer(normalBuffers.get(i), ElementType.Vector3, ComponentType.Float, false));
        vertexBuffers.put(Semantic.Tangent, new VertexBuffer(tangentBuffers.get(i), ElementType.Vector4, ComponentType.Float, false));
        vertexBuffers.put(Semantic.TexCoord0, new VertexBuffer(texCoord0Buffers.get(i), ElementType.Vector2, ComponentType.Float, false));
        if (hasWeights) {
            vertexBuffers.put(Semantic.Joints0, new VertexBuffer(colorBuffers.get(i), ElementType.Vector4, ComponentType.UnsignedByte, false));
            vertexBuffers.put(Semantic.Weights0, new VertexBuffer(weightBuffers.get(i), ElementType.Vector4, ComponentType.UnsignedByte, true));
        } else {
            vertexBuffers.put(Semantic.Color0, new VertexBuffer(colorBuffers.get(i), ElementType.Vector4, ComponentType.UnsignedByte, true));
        }
        return new Mesh(faceBuffer, vertexBuffers, -1);
    }
}
