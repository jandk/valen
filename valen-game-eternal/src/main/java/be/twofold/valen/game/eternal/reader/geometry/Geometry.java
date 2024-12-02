package be.twofold.valen.game.eternal.reader.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.nio.*;

public final class Geometry {
    private static final byte[] WeightTableZ = new byte[16];
    private static final byte[] WeightTableW = new byte[16];

    static {
        for (int i = 0; i < 16; i++) {
            WeightTableZ[i] = MathF.packUNorm8(i / 45.0f);
            WeightTableW[i] = MathF.packUNorm8(i / 60.0f);
        }
    }

    private Geometry() {
    }

    public static Geo.Reader<FloatBuffer> readPosition(float scale, Vector3 offset) {
        return (source, dst) -> Vector3.read(source).fma(scale, offset).toBuffer(dst);
    }

    public static Geo.Reader<FloatBuffer> readPackedPosition(float scale, Vector3 offset) {
        return (source, dst) -> readVector4UNorm16(source).toVector3().fma(scale, offset).toBuffer(dst);
    }

    public static Geo.Reader<FloatBuffer> readPackedNormal() {
        return (source, dst) -> readVector3UNorm8Normal(source).normalize().toBuffer(dst);
    }

    public static Geo.Reader<FloatBuffer> readPackedTangent() {
        return (source, dst) -> {
            source.skip(4); // skip normal

            Vector3 xyz = readVector3UNorm8Normal(source).normalize();
            float w = Float.intBitsToFloat(((source.readByte() & 0x80) << 24) | 0x3F800000);
            new Vector4(xyz, w).toBuffer(dst);
        };
    }

    public static Geo.Reader<ByteBuffer> readWeight() {
        return (source, dst) -> {
            source.skip(3); // skip normal
            byte wn = source.readByte();
            source.skip(3); // skip tangent
            byte wt = source.readByte();

            byte y = (byte) (wt & 0x7f);
            byte z = WeightTableZ[(wn & 0xf0) >>> 4];
            byte w = WeightTableW[(wn & 0x0f)];
            byte x = (byte) (255 - y - z - w);

            dst.put(x);
            dst.put(y);
            dst.put(z);
            dst.put(w);
        };
    }

    public static Geo.Reader<FloatBuffer> readUV(float scale, Vector2 offset) {
        return (source, dst) -> Vector2.read(source).fma(scale, offset).toBuffer(dst);
    }

    public static Geo.Reader<FloatBuffer> readPackedUV(float scale, Vector2 offset) {
        return (source, dst) -> readVector2UNorm16(source).fma(scale, offset).toBuffer(dst);
    }

    public static Geo.Reader<ByteBuffer> readColor() {
        return (source, dst) -> {
            dst.put(source.readByte());
            dst.put(source.readByte());
            dst.put(source.readByte());
            dst.put(source.readByte());
        };
    }

    public static Geo.Reader<ShortBuffer> readFace() {
        return (source, dst) -> dst.put(source.readShort());
    }

    private static Vector2 readVector2UNorm16(DataSource source) throws IOException {
        float x = MathF.unpackUNorm16(source.readShort());
        float y = MathF.unpackUNorm16(source.readShort());
        return new Vector2(x, y);
    }

    private static Vector3 readVector3UNorm8Normal(DataSource source) throws IOException {
        float x = MathF.unpackUNorm8Normal(source.readByte());
        float y = MathF.unpackUNorm8Normal(source.readByte());
        float z = MathF.unpackUNorm8Normal(source.readByte());
        return new Vector3(x, y, z);
    }

    private static Vector4 readVector4UNorm16(DataSource source) throws IOException {
        float x = MathF.unpackUNorm16(source.readShort());
        float y = MathF.unpackUNorm16(source.readShort());
        float z = MathF.unpackUNorm16(source.readShort());
        float w = MathF.unpackUNorm16(source.readShort());
        return new Vector4(x, y, z, w);
    }
}
