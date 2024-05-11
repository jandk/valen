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
    private final List<FloatBuffer> texCoordBuffers = new ArrayList<>();
    private final List<ByteBuffer> colorBuffers = new ArrayList<>();
    private final List<ByteBuffer> jointBuffers = new ArrayList<>();
    private final List<ByteBuffer> weightBuffers = new ArrayList<>();
    private final List<ShortBuffer> indexBuffers = new ArrayList<>();

    public GeometryReader(boolean hasWeights) {
        this.hasWeights = hasWeights;
    }

    public List<Mesh> readMeshes(DataSource source, List<LodInfo> lods, List<GeometryMemoryLayout> layouts) throws IOException {
        for (var layout : layouts) {
            assert layout.normalMask() == 0x14 : "Unknown normal mask: " + layout.normalMask();
            assert layout.colorMask() == 0x08 : "Unknown color mask: " + layout.colorMask();
        }

        for (var layout : layouts) {
            source.seek(layout.positionOffset());
            for (var lod : lods) {
                if (lod.flags() == layout.combinedVertexMask()) {
                    var buffer = switch (layout.positionMask()) {
                        case 0x01 -> readVertices(source, lod);
                        case 0x20 -> readPackedVertices(source, lod);
                        default -> throw new RuntimeException("Unknown position mask: " + layout.positionMask());
                    };
                    positionBuffers.add(buffer);
                }
            }
        }

        for (var layout : layouts) {
            source.seek(layout.normalOffset());
            for (var lod : lods) {
                if (lod.flags() == layout.combinedVertexMask()) {
                    normalBuffers.add(readPackedNormals(source, lod));
                }
            }
        }

        for (var layout : layouts) {
            source.seek(layout.normalOffset());
            for (LodInfo lod : lods) {
                if (lod.flags() == layout.combinedVertexMask()) {
                    tangentBuffers.add(readPackedTangents(source, lod));
                }
            }
        }

        for (var layout : layouts) {
            source.seek(layout.normalOffset());
            for (LodInfo lod : lods) {
                if (lod.flags() == layout.combinedVertexMask()) {
                    weightBuffers.add(readWeights(source, lod));
                }
            }
        }

        for (var layout : layouts) {
            source.seek(layout.uvOffset());
            for (LodInfo lod : lods) {
                if (lod.flags() == layout.combinedVertexMask()) {
                    var buffer = switch (layout.uvMask()) {
                        case 0x08000 -> readUVs(source, lod);
                        case 0x20000 -> readPackedUVs(source, lod);
                        default -> throw new RuntimeException("Unknown UV mask: " + layout.normalMask());
                    };
                    texCoordBuffers.add(buffer);
                }
            }
        }

        for (var layout : layouts) {
            source.seek(layout.colorOffset());
            for (LodInfo lod : lods) {
                if (lod.flags() == layout.combinedVertexMask()) {
                    ByteBuffer bb = readColors(source, lod);
                    if (hasWeights) {
                        jointBuffers.add(bb);
                    } else {
                        colorBuffers.add(bb);
                    }
                }
            }
        }

        for (var layout : layouts) {
            source.seek(layout.indexOffset());
            for (LodInfo lod : lods) {
                if (lod.flags() == layout.combinedVertexMask()) {
                    indexBuffers.add(readFaces(source, lod));
                }
            }
        }

        return IntStream.range(0, lods.size())
            .mapToObj(this::getMesh)
            .toList();
    }

    public FloatBuffer readVertices(DataSource source, LodInfo lod) throws IOException {
        var dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readVertex(source, dst, lod.vertexOffset(), lod.vertexScale());
        }
        return dst.flip();
    }

    public FloatBuffer readPackedVertices(DataSource source, LodInfo lod) throws IOException {
        var dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (var i = 0; i < lod.numVertices(); i++) {
            Geometry.readPackedVertex(source, dst, lod.vertexOffset(), lod.vertexScale());
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

    public ShortBuffer readFaces(DataSource source, LodInfo lod) throws IOException {
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
