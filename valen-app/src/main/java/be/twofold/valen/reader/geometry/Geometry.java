package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

import java.nio.*;

public final class Geometry {
    private static final byte[] WeightTableZ = {
        0x00, 0x06, 0x0b, 0x11, 0x17, 0x1c, 0x22, 0x28,
        0x2d, 0x33, 0x39, 0x3e, 0x44, 0x4a, 0x4f, 0x55
    };

    private static final byte[] WeightTableW = {
        0x00, 0x04, 0x09, 0x0d, 0x11, 0x15, 0x1a, 0x1e,
        0x22, 0x26, 0x2b, 0x2f, 0x33, 0x37, 0x3c, 0x40
    };

    private Geometry() {
    }

    public static void readVertex(BetterBuffer src, FloatBuffer dst, Vector3 offset, float scale) {
        src.getVector3().multiply(scale).add(offset).put(dst);
    }

    public static void readPackedVertex(BetterBuffer src, FloatBuffer dst, Vector3 offset, float scale) {
        float x = toUNorm(src.getShort());
        float y = toUNorm(src.getShort());
        float z = toUNorm(src.getShort());
        src.skip(2);

        new Vector3(x, y, z).multiply(scale).add(offset).put(dst);
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

    public static void readWeight(BetterBuffer src, ByteBuffer dst) {
        src.skip(3); // skip normal
        byte wn = src.getByte();
        src.skip(3); // skip tangent
        byte wt = src.getByte();

        byte y = (byte) (wt & 0x7f);
        byte z = WeightTableZ[(wn & 0xf0) >>> 4];
        byte w = WeightTableW[wn & 0xf];
        byte x = (byte) (255 - y - z - w);

        dst.put(x);
        dst.put(y);
        dst.put(z);
        dst.put(w);
    }

    public static void readUV(BetterBuffer src, FloatBuffer dst, Vector2 offset, float scale) {
        src.getVector2().multiply(scale).add(offset).put(dst);
    }

    public static void readPackedUV(BetterBuffer src, FloatBuffer dst, Vector2 offset, float scale) {
        float u = toUNorm(src.getShort());
        float v = toUNorm(src.getShort());

        new Vector2(u, v).multiply(scale).add(offset).put(dst);
    }

    private static float toSNorm(byte b) {
        return (Byte.toUnsignedInt(b) / 255f) * 2 - 1;
    }

    private static float toUNorm(short s) {
        return Short.toUnsignedInt(s) / 65535f;
    }
}
