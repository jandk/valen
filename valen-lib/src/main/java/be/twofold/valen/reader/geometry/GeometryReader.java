package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;
import java.util.stream.*;

public final class GeometryReader {
    private final boolean hasWeights;
    private final List<FloatBuffer> positionBuffers = new ArrayList<>();
    private final List<FloatBuffer> normalBuffers = new ArrayList<>();
    private final List<FloatBuffer> tangentBuffers = new ArrayList<>();
    private final List<FloatBuffer> texCoordBuffers = new ArrayList<>();
    private final List<ByteBuffer> colorBuffers = new ArrayList<>();
    private final List<ByteBuffer> jointBuffers = new ArrayList<>();
    private final List<ByteBuffer> weightBuffers = new ArrayList<>();
    private final List<ShortBuffer> indexBuffers = new ArrayList<>();

    public GeometryReader(boolean hasWeights) {
        this.hasWeights = hasWeights;
    }

    public List<Mesh> readMeshes(BetterBuffer buffer, List<LodInfo> lods, List<GeometryMemoryLayout> layouts) {
        for (var layout : layouts) {
            assert layout.normalMask() == 0x14 : "Unknown normal mask: " + layout.normalMask();
            assert layout.colorMask() == 0x08 : "Unknown color mask: " + layout.colorMask();
        }

        for (var layout : layouts) {
            buffer.position(layout.positionOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> switch (layout.positionMask()) {
                    case 0x01 -> readVertices(buffer, lod);
                    case 0x20 -> readPackedVertices(buffer, lod);
                    default -> throw new RuntimeException("Unknown position mask: " + layout.positionMask());
                })
                .forEach(positionBuffers::add);
        }

        for (var layout : layouts) {
            buffer.position(layout.normalOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> readPackedNormals(buffer, lod))
                .forEach(normalBuffers::add);
        }

        for (var layout : layouts) {
            buffer.position(layout.normalOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> readPackedTangents(buffer, lod))
                .forEach(tangentBuffers::add);
        }

        for (var layout : layouts) {
            buffer.position(layout.normalOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> readWeights(buffer, lod))
                .forEach(weightBuffers::add);
        }

        for (var layout : layouts) {
            buffer.position(layout.uvOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> switch (layout.uvMask()) {
                    case 0x08000 -> readUVs(buffer, lod);
                    case 0x20000 -> readPackedUVs(buffer, lod);
                    default -> throw new RuntimeException("Unknown UV mask: " + layout.normalMask());
                })
                .forEach(texCoordBuffers::add);
        }

        for (var layout : layouts) {
            buffer.position(layout.colorOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> readColors(buffer, lod))
                .forEach(bb -> {
                    if (hasWeights) {
                        jointBuffers.add(bb);
                    } else {
                        colorBuffers.add(bb);
                    }
                });
        }

        for (var layout : layouts) {
            buffer.position(layout.indexOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> readFaces(buffer, lod))
                .forEach(indexBuffers::add);
        }

        return IntStream.range(0, lods.size())
            .mapToObj(this::getMesh)
            .toList();
    }

    public FloatBuffer readVertices(BetterBuffer src, LodInfo lod) {
        var dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readVertex(src, dst, lod.vertexOffset(), lod.vertexScale());
        }
        return dst.flip();
    }

    public FloatBuffer readPackedVertices(BetterBuffer src, LodInfo lod) {
        var dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedVertex(src, dst, lod.vertexOffset(), lod.vertexScale());
        }
        return dst.flip();
    }

    public FloatBuffer readPackedNormals(BetterBuffer src, LodInfo lod) {
        var dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedNormal(src, dst);
        }
        return dst.flip();
    }

    public FloatBuffer readPackedTangents(BetterBuffer src, LodInfo lod) {
        var dst = FloatBuffer.allocate(lod.numVertices() * 4);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedTangent(src, dst);
        }
        return dst.flip();
    }

    public ByteBuffer readWeights(BetterBuffer src, LodInfo lod) {
        var dst = ByteBuffer.allocate(lod.numVertices() * 4);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readWeight(src, dst);
        }
        return dst.flip();
    }

    public FloatBuffer readUVs(BetterBuffer src, LodInfo lod) {
        var dst = FloatBuffer.allocate(lod.numVertices() * 2);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readUV(src, dst, lod.uvOffset(), lod.uvScale());
        }
        return dst.flip();
    }

    public FloatBuffer readPackedUVs(BetterBuffer src, LodInfo lod) {
        var dst = FloatBuffer.allocate(lod.numVertices() * 2);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedUV(src, dst, lod.uvOffset(), lod.uvScale());
        }
        return dst.flip();
    }

    public ByteBuffer readColors(BetterBuffer src, LodInfo lod) {
        var dst = ByteBuffer.allocate(lod.numVertices() * 4);
        dst.put(src.getBytes(lod.numVertices() * 4));
        return dst.flip();
    }

    public ShortBuffer readFaces(BetterBuffer src, LodInfo lod) {
        var dst = ShortBuffer.allocate(lod.numFaces() * 3);
        for (var i = 0; i < lod.numFaces(); i++) {
            var f1 = src.getShort();
            var f2 = src.getShort();
            var f3 = src.getShort();

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
        vertexBuffers.put(Semantic.TexCoord, new VertexBuffer(texCoordBuffers.get(i), ElementType.Vector2, ComponentType.Float, false));
        if (hasWeights) {
            vertexBuffers.put(Semantic.Joints, new VertexBuffer(jointBuffers.get(i), ElementType.Vector4, ComponentType.UnsignedByte, false));
            vertexBuffers.put(Semantic.Weights, new VertexBuffer(weightBuffers.get(i), ElementType.Vector4, ComponentType.UnsignedByte, true));
        } else {
            vertexBuffers.put(Semantic.Color, new VertexBuffer(colorBuffers.get(i), ElementType.Vector4, ComponentType.UnsignedByte, true));
        }
        return new Mesh(faceBuffer, vertexBuffers, -1);
    }
}
