package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;
import java.util.stream.*;

public final class GeometryReader {
    private final boolean hasWeights;
    private final List<ByteBuffer> positionBuffers = new ArrayList<>();
    private final List<ByteBuffer> normalBuffers = new ArrayList<>();
    private final List<ByteBuffer> tangentBuffers = new ArrayList<>();
    private final List<ByteBuffer> texCoordBuffers = new ArrayList<>();
    private final List<ByteBuffer> colorBuffers = new ArrayList<>();
    private final List<ByteBuffer> jointBuffers = new ArrayList<>();
    private final List<ByteBuffer> weightBuffers = new ArrayList<>();
    private final List<ByteBuffer> indexBuffers = new ArrayList<>();

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

    public ByteBuffer readVertices(BetterBuffer src, LodInfo lod) {
        var buffer = Buffers.allocateFloat(lod.numVertices() * 3);
        var dst = buffer.asFloatBuffer();
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readVertex(src, dst, lod.vertexOffset(), lod.vertexScale());
        }
        return buffer.clear();
    }

    public ByteBuffer readPackedVertices(BetterBuffer src, LodInfo lod) {
        var buffer = Buffers.allocateFloat(lod.numVertices() * 3);
        var dst = buffer.asFloatBuffer();
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedVertex(src, dst, lod.vertexOffset(), lod.vertexScale());
        }
        return buffer.clear();
    }

    public ByteBuffer readPackedNormals(BetterBuffer src, LodInfo lod) {
        var buffer = Buffers.allocateFloat(lod.numVertices() * 3);
        var dst = buffer.asFloatBuffer();
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedNormal(src, dst);
            src.skip(4); // skip tangents
        }
        return buffer.clear();
    }

    public ByteBuffer readPackedTangents(BetterBuffer src, LodInfo lod) {
        var buffer = Buffers.allocateFloat(lod.numVertices() * 4);
        var dst = buffer.asFloatBuffer();
        for (var i = 0; i < lod.numVertices(); i++) {
            src.skip(4); // skip normals
            Geometry.readPackedTangent(src, dst);
        }
        return buffer.clear();
    }

    public ByteBuffer readWeights(BetterBuffer src, LodInfo lod) {
        var dst = ByteBuffer.allocate(lod.numVertices() * 4);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readWeight(src, dst);
        }
        return dst.clear();
    }

    public ByteBuffer readUVs(BetterBuffer src, LodInfo lod) {
        var buffer = Buffers.allocateFloat(lod.numVertices() * 2);
        var dst = buffer.asFloatBuffer();
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readUV(src, dst, lod.uvOffset(), lod.uvScale());
        }
        return buffer.clear();
    }

    public ByteBuffer readPackedUVs(BetterBuffer src, LodInfo lod) {
        var buffer = Buffers.allocateFloat(lod.numVertices() * 2);
        var dst = buffer.asFloatBuffer();
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedUV(src, dst, lod.uvOffset(), lod.uvScale());
        }
        return buffer.clear();
    }

    public ByteBuffer readColors(BetterBuffer buffer, LodInfo lod) {
        var dst = ByteBuffer.allocate(lod.numVertices() * 4);
        dst.put(buffer.getBytes(lod.numVertices() * 4));
        return dst.clear();
    }

    public ByteBuffer readFaces(BetterBuffer src, LodInfo lod) {
        var buffer = Buffers.allocateShort(lod.numFaces() * 3);
        var dst = buffer.asShortBuffer();
        for (var i = 0; i < lod.numFaces(); i++) {
            dst.put(src.getShort());
            dst.put(src.getShort());
            dst.put(src.getShort());
        }
        return buffer.clear();
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
        return new Mesh(faceBuffer, vertexBuffers);
    }
}
