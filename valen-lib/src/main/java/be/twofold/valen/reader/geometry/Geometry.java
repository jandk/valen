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
        dst.put(Math.fma(src.getFloat(), scale, offset.x()));
        dst.put(Math.fma(src.getFloat(), scale, offset.y()));
        dst.put(Math.fma(src.getFloat(), scale, offset.z()));
    }

    public static void readPackedVertex(BetterBuffer src, FloatBuffer dst, Vector3 offset, float scale) {
        dst.put(Math.fma(MathF.unpackUNorm16(src.getShort()), scale, offset.x()));
        dst.put(Math.fma(MathF.unpackUNorm16(src.getShort()), scale, offset.y()));
        dst.put(Math.fma(MathF.unpackUNorm16(src.getShort()), scale, offset.z()));
        src.expectShort(0);
    }

    public static void readPackedNormal(BetterBuffer src, FloatBuffer dst) {
        float x = MathF.unpack8(src.getByte());
        float y = MathF.unpack8(src.getByte());
        float z = MathF.unpack8(src.getByte());

        float scale = 1.0f / MathF.sqrt(x * x + y * y + z * z);

        dst.put(x * scale);
        dst.put(y * scale);
        dst.put(z * scale);

        src.skip(5); // skip tangent
    }

    public static void readPackedTangent(BetterBuffer src, FloatBuffer dst) {
        src.skip(4); // skip normal

        float x = MathF.unpack8(src.getByte());
        float y = MathF.unpack8(src.getByte());
        float z = MathF.unpack8(src.getByte());
        float w = (src.getByte() & 0x80) == 0 ? 1 : -1;

        float scale = 1.0f / MathF.sqrt(x * x + y * y + z * z);

        dst.put(x * scale);
        dst.put(y * scale);
        dst.put(z * scale);
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
        dst.put(Math.fma(src.getFloat(), scale, offset.x()));
        dst.put(Math.fma(src.getFloat(), scale, offset.y()));
    }

    public static void readPackedUV(BetterBuffer src, FloatBuffer dst, Vector2 offset, float scale) {
        dst.put(Math.fma(MathF.unpackUNorm16(src.getShort()), scale, offset.x()));
        dst.put(Math.fma(MathF.unpackUNorm16(src.getShort()), scale, offset.y()));
    }
}
