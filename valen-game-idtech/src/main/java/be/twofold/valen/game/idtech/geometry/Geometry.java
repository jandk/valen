package be.twofold.valen.game.idtech.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;
import java.nio.*;

public final class Geometry {
    private Geometry() {
    }

    public static GeoReader<FloatBuffer> readPosition(float scale, Vector3 offset) {
        return (source, dst) -> Vector3.read(source).fma(scale, offset).toBuffer(dst);
    }

    public static GeoReader<FloatBuffer> readPackedPosition(float scale, Vector3 offset) {
        return (source, dst) -> readVector4UNorm16(source).toVector3().fma(scale, offset).toBuffer(dst);
    }

    public static GeoReader<FloatBuffer> readPackedNormal() {
        return (source, dst) -> readVector3UNorm8Normal(source).normalize().toBuffer(dst);
    }

    public static GeoReader<FloatBuffer> readPackedTangent() {
        return (source, dst) -> {
            source.skip(4); // skip normal

            Vector3 xyz = readVector3UNorm8Normal(source).normalize();
            float w = Float.intBitsToFloat(((source.readByte() & 0x80) << 24) | 0x3F800000);
            new Vector4(xyz, w).toBuffer(dst);
        };
    }

    public static GeoReader<FloatBuffer> readWeight4(boolean write0) {
        return readNormalTangentWeights(45.0f, 60.0f, write0);
    }

    public static GeoReader<FloatBuffer> readWeight6() {
        return readNormalTangentWeights(75.0f, 89.0f, false);
    }

    public static GeoReader<FloatBuffer> readWeight8() {
        return readNormalTangentWeights(104.0f, 120.0f, false);
    }

    private static GeoReader<FloatBuffer> readNormalTangentWeights(float scale1, float scale2, boolean write0) {
        float factor1 = 1.0f / scale1;
        float factor2 = 1.0f / scale2;
        return (source, dst) -> {
            source.skip(3); // skip normal
            byte wn = source.readByte();
            source.skip(3); // skip tangent
            byte wt = source.readByte();

            float y = MathF.unpackUNorm8((byte) (wt & 0x7f));
            float z = ((wn & 0xf0) >>> 4) * factor1;
            float w = ((wn & 0x0f)) * factor2;

            if (write0) {
                dst.put(1.0f - y - z - w);
            }

            dst.put(y);
            dst.put(z);
            dst.put(w);
        };
    }

    public static GeoReader<ShortBuffer> readBone1() {
        return (source, dst) -> {
            source.skip(3);
            dst.put((short) Byte.toUnsignedInt(source.readByte()));
        };
    }

    public static GeoReader<FloatBuffer> readUV(float scale, Vector2 offset) {
        return (source, dst) -> Vector2.read(source).fma(scale, offset).toBuffer(dst);
    }

    public static GeoReader<FloatBuffer> readPackedUV(float scale, Vector2 offset) {
        return (source, dst) -> readVector2UNorm16(source).fma(scale, offset).toBuffer(dst);
    }

    public static GeoReader<ShortBuffer> readFaceIndex() {
        return copyShorts(1);
    }

    public static GeoReader<ShortBuffer> copyBytesAsShorts(int n) {
        return (source, dst) -> {
            for (int i = 0; i < n; i++) {
                dst.put((short) Byte.toUnsignedInt(source.readByte()));
            }
        };
    }

    public static GeoReader<FloatBuffer> copyBytesAsFloats(int n) {
        return (source, dst) -> {
            for (int i = 0; i < n; i++) {
                dst.put(MathF.unpackUNorm8(source.readByte()));
            }
        };
    }

    public static GeoReader<ByteBuffer> copyBytes(int n) {
        return (source, dst) -> {
            for (int i = 0; i < n; i++) {
                dst.put(source.readByte());
            }
        };
    }

    public static GeoReader<ShortBuffer> copyShorts(int n) {
        return (source, dst) -> {
            for (int i = 0; i < n; i++) {
                dst.put(source.readShort());
            }
        };
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
