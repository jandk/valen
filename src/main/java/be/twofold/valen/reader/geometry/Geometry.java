package be.twofold.valen.reader.geometry;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;
import be.twofold.valen.reader.model.*;

import java.nio.*;

public final class Geometry {
    private Geometry() {
    }

    public static FloatBuffer readVertices(BetterBuffer src, ModelLodInfo lodInfo) {
        Vector3 offset = lodInfo.vertexOffset();

        FloatBuffer dst = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        for (int i = 0; i < lodInfo.numVertices(); i++) {
            readVertex(src, dst, offset.x(), offset.y(), offset.z(), lodInfo.vertexScale());
        }
        return dst.flip();
    }

    public static FloatBuffer readPackedVertices(BetterBuffer src, ModelLodInfo lodInfo) {
        Vector3 offset = lodInfo.vertexOffset();

        FloatBuffer dst = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        for (int i = 0; i < lodInfo.numVertices(); i++) {
            readPackedVertex(src, dst, offset.x(), offset.y(), offset.z(), lodInfo.vertexScale());
        }
        return dst.flip();
    }

    public static FloatBuffer readPackedNormals(BetterBuffer src, ModelLodInfo lodInfo) {
        FloatBuffer dst = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        for (int i = 0; i < lodInfo.numVertices(); i++) {
            readPackedNormal(src, dst);
            src.skip(4); // skip tangents
        }
        return dst.flip();
    }

    public static FloatBuffer readPackedTangents(BetterBuffer src, ModelLodInfo lodInfo) {
        FloatBuffer dst = FloatBuffer.allocate(lodInfo.numVertices() * 3);
        for (int i = 0; i < lodInfo.numVertices(); i++) {
            src.skip(4); // skip normals
            readPackedTangent(src, dst);
        }
        return dst.flip();
    }

    public static FloatBuffer readUVs(BetterBuffer src, ModelLodInfo lodInfo) {
        Vector2 offset = lodInfo.uvMapOffset();

        FloatBuffer dst = FloatBuffer.allocate(lodInfo.numVertices() * 2);
        for (int i = 0; i < lodInfo.numVertices(); i++) {
            readUV(src, dst, offset.x(), offset.y(), lodInfo.uvScale());
        }
        return dst.flip();
    }

    public static FloatBuffer readPackedUVs(BetterBuffer src, ModelLodInfo lodInfo) {
        Vector2 offset = lodInfo.uvMapOffset();

        FloatBuffer dst = FloatBuffer.allocate(lodInfo.numVertices() * 2);
        for (int i = 0; i < lodInfo.numVertices(); i++) {
            readPackedUV(src, dst, offset.x(), offset.y(), lodInfo.uvScale());
        }
        return dst.flip();
    }

    public static ShortBuffer readFaces(BetterBuffer src, ModelLodInfo lodInfo) {
        ShortBuffer dst = ShortBuffer.allocate(lodInfo.numEdges());
        for (int i = 0; i < lodInfo.numEdges(); i += 3) {
            dst.put(src.getShort());
            dst.put(src.getShort());
            dst.put(src.getShort());
        }
        return dst.flip();
    }

    public static void readVertex(BetterBuffer src, FloatBuffer dst, float offsetX, float offsetY, float offsetZ, float scale) {
        float x = src.getFloat() * scale + offsetX;
        float y = src.getFloat() * scale + offsetY;
        float z = src.getFloat() * scale + offsetZ;

        dst.put(x);
        dst.put(y);
        dst.put(z);
    }

    public static void readPackedVertex(BetterBuffer src, FloatBuffer dst, float offsetX, float offsetY, float offsetZ, float scale) {
        float px = Short.toUnsignedInt(src.getShort());
        float py = Short.toUnsignedInt(src.getShort());
        float pz = Short.toUnsignedInt(src.getShort());
        src.skip(2);

        float x = (px / 65535f) * scale + offsetX;
        float y = (py / 65535f) * scale + offsetY;
        float z = (pz / 65535f) * scale + offsetZ;

        dst.put(x);
        dst.put(y);
        dst.put(z);
    }

    public static void readPackedNormal(BetterBuffer src, FloatBuffer nDst) {
        float packedXn = Byte.toUnsignedInt(src.getByte());
        float packedYn = Byte.toUnsignedInt(src.getByte());
        float packedZn = Byte.toUnsignedInt(src.getByte());
        src.skip(1);

        float x = (packedXn / 255) * 2 - 1;
        float y = (packedYn / 255) * 2 - 1;
        float z = (packedZn / 255) * 2 - 1;

        // Normalize, as we have low accuracy
        float scale = (float) (1 / Math.sqrt(x * x + y * y + z * z));
        nDst.put(x * scale);
        nDst.put(y * scale);
        nDst.put(z * scale);
    }

    public static void readPackedTangent(BetterBuffer src, FloatBuffer dst) {
        float packedXn = Byte.toUnsignedInt(src.getByte());
        float packedYn = Byte.toUnsignedInt(src.getByte());
        float packedZn = Byte.toUnsignedInt(src.getByte());
        float w = (src.getByte() & 0x80) == 0 ? 1 : -1; // Could be the other way around

        float x = (packedXn / 255) * 2 - 1;
        float y = (packedYn / 255) * 2 - 1;
        float z = (packedZn / 255) * 2 - 1;

        // Normalize, as we have low accuracy
        float scale = (float) (1 / Math.sqrt(x * x + y * y + z * z));
        dst.put(x * scale);
        dst.put(y * scale);
        dst.put(z * scale);
        dst.put(w);
    }

    public static void readUV(BetterBuffer src, FloatBuffer dst, float offsetU, float offsetV, float scale) {
        float u = src.getFloat() * scale + offsetU;
        float v = src.getFloat() * scale + offsetV;

        dst.put(u);
        dst.put(v);
    }

    public static void readPackedUV(BetterBuffer src, FloatBuffer dst, float offsetU, float offsetV, float scale) {
        float pu = Short.toUnsignedInt(src.getShort());
        float pv = Short.toUnsignedInt(src.getShort());

        float u = (pu / 65535) * scale + offsetU;
        float v = (pv / 65535) * scale + offsetV;
        // float v = Math.abs(((Math.abs(pv / 65535)) * scale) - (1 - offsetV));
        dst.put(u);
        dst.put(v);
    }
}
