package be.twofold.valen.reader.geometry;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;

import java.nio.*;

public final class Geometry {
    private Geometry() {
    }

    public static FloatBuffer readVertices(BetterBuffer src, LodInfo lod) {
        FloatBuffer dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (int i = 0; i < lod.numVertices(); i++) {
            readVertex(src, dst, lod.vertexOffset(), lod.vertexScale());
        }
        return dst.flip();
    }

    public static FloatBuffer readPackedVertices(BetterBuffer src, LodInfo lod) {
        FloatBuffer dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (int i = 0; i < lod.numVertices(); i++) {
            readPackedVertex(src, dst, lod.vertexOffset(), lod.vertexScale());
        }
        return dst.flip();
    }

    public static FloatBuffer readPackedNormals(BetterBuffer src, LodInfo lod) {
        FloatBuffer dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (int i = 0; i < lod.numVertices(); i++) {
            readPackedNormal(src, dst);
            src.skip(4); // skip tangents
        }
        return dst.flip();
    }

    public static FloatBuffer readPackedTangents(BetterBuffer src, LodInfo lod) {
        FloatBuffer dst = FloatBuffer.allocate(lod.numVertices() * 3);
        for (int i = 0; i < lod.numVertices(); i++) {
            src.skip(4); // skip normals
            readPackedTangent(src, dst);
        }
        return dst.flip();
    }

    public static FloatBuffer readUVs(BetterBuffer src, LodInfo lod) {
        FloatBuffer dst = FloatBuffer.allocate(lod.numVertices() * 2);
        for (int i = 0; i < lod.numVertices(); i++) {
            readUV(src, dst, lod.uvOffset(), lod.uvScale());
        }
        return dst.flip();
    }

    public static FloatBuffer readPackedUVs(BetterBuffer src, LodInfo lod) {
        FloatBuffer dst = FloatBuffer.allocate(lod.numVertices() * 2);
        for (int i = 0; i < lod.numVertices(); i++) {
            readPackedUV(src, dst, lod.uvOffset(), lod.uvScale());
        }
        return dst.flip();
    }

    public static ShortBuffer readFaces(BetterBuffer src, LodInfo lod) {
        ShortBuffer dst = ShortBuffer.allocate(lod.numEdges());
        for (int i = 0; i < lod.numEdges(); i += 3) {
            dst.put(src.getShort());
            dst.put(src.getShort());
            dst.put(src.getShort());
        }
        return dst.flip();
    }

    public static void readVertex(BetterBuffer src, FloatBuffer dst, Vector3 offset, float scale) {
        src.getVector3().mul(scale).add(offset).put(dst);
    }

    public static void readPackedVertex(BetterBuffer src, FloatBuffer dst, Vector3 offset, float scale) {
        float x = toUNorm(src.getShort());
        float y = toUNorm(src.getShort());
        float z = toUNorm(src.getShort());
        src.skip(2);

        new Vector3(x, y, z).mul(scale).add(offset).put(dst);
    }

    public static void readPackedNormal(BetterBuffer src, FloatBuffer nDst) {
        float x = toSNorm(src.getByte());
        float y = toSNorm(src.getByte());
        float z = toSNorm(src.getByte());
        src.skip(1);

        new Vector3(x, y, z).normalize().put(nDst);
    }

    public static void readPackedTangent(BetterBuffer src, FloatBuffer dst) {
        float x = toSNorm(src.getByte());
        float y = toSNorm(src.getByte());
        float z = toSNorm(src.getByte());
        float w = (src.getByte() & 0x80) == 0 ? 1 : -1;

        new Vector3(x, y, z).normalize().put(dst);
        dst.put(w);
    }

    public static void readUV(BetterBuffer src, FloatBuffer dst, Vector2 offset, float scale) {
        src.getVector2().mul(scale).add(offset).put(dst);
    }

    public static void readPackedUV(BetterBuffer src, FloatBuffer dst, Vector2 offset, float scale) {
        float u = toUNorm(src.getShort());
        float v = toUNorm(src.getShort());

        new Vector2(u, v).mul(scale).add(offset).put(dst);
    }

    private static float toSNorm(byte b) {
        return (Byte.toUnsignedInt(b) / 255f) * 2 - 1;
    }

    private static float toUNorm(short s) {
        return Short.toUnsignedInt(s) / 65535f;
    }
}
