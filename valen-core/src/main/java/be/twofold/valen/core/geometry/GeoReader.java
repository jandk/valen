package be.twofold.valen.core.geometry;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;

@FunctionalInterface
public interface GeoReader<T> {
    void read(BinaryReader source, T target, int offset) throws IOException;

    static GeoReader<MutableFloats> readPosition(float scale, Vector3 bias) {
        return (source, target, offset) -> Vector3.read(source).fma(scale, bias).toFloats(target, offset);
    }

    static GeoReader<MutableFloats> readPackedPosition(float scale, Vector3 bias) {
        return (source, target, offset) -> {
            float x = MathF.unpackUNorm16(source.readShort());
            float y = MathF.unpackUNorm16(source.readShort());
            float z = MathF.unpackUNorm16(source.readShort());
            source.skip(2);

            new Vector3(x, y, z).fma(scale, bias).toFloats(target, offset);
        };
    }

    static GeoReader<MutableFloats> readPackedNormal() {
        return (source, target, offset) -> readVector3UNorm8Normal(source).normalize().toFloats(target, offset);
    }

    static GeoReader<MutableFloats> readPackedTangent() {
        return (source, target, offset) -> {
            source.skip(4); // skip normal

            Vector3 xyz = readVector3UNorm8Normal(source).normalize();
            float w = Float.intBitsToFloat(((source.readByte() & 0x80) << 24) | 0x3F800000);
            new Vector4(xyz, w).toFloats(target, offset);
        };
    }

    static GeoReader<MutableFloats> readWeight4() {
        return readNormalTangentWeights(45.0f, 60.0f);
    }

    static GeoReader<MutableFloats> readWeight6() {
        return readNormalTangentWeights(75.0f, 89.0f);
    }

    static GeoReader<MutableFloats> readWeight8() {
        return readNormalTangentWeights(104.0f, 120.0f);
    }

    private static GeoReader<MutableFloats> readNormalTangentWeights(float scale1, float scale2) {
        float factor1 = 1.0f / scale1;
        float factor2 = 1.0f / scale2;
        return (source, target, offset) -> {
            source.skip(3); // skip normal
            byte wn = source.readByte();
            source.skip(3); // skip tangent
            byte wt = source.readByte();

            float y = MathF.unpackUNorm8((byte) (wt & 0x7f));
            float z = ((wn & 0xf0) >>> 4) * factor1;
            float w = ((wn & 0x0f)) * factor2;

            target.setFloat(offset/**/, 1.0f - y - z - w);
            target.setFloat(offset + 1, y);
            target.setFloat(offset + 2, z);
            target.setFloat(offset + 3, w);
        };
    }

    static GeoReader<MutableShorts> readBone1() {
        return (source, target, offset) -> {
            source.skip(3);
            target.setShort(offset, (short) Byte.toUnsignedInt(source.readByte()));
        };
    }

    static GeoReader<MutableFloats> readUV(float scale, Vector2 bias) {
        return (source, target, offset) -> {
            Vector2.read(source).fma(scale, bias).toFloats(target, offset);
        };
    }

    static GeoReader<MutableFloats> readPackedUV(float scale, Vector2 bias) {
        return (source, target, offset) -> {
            float x = MathF.unpackUNorm16(source.readShort());
            float y = MathF.unpackUNorm16(source.readShort());
            new Vector2(x, y).fma(scale, bias).toFloats(target, offset);
        };
    }

    static GeoReader<MutableInts> readShortAsInt() {
        return (source, target, offset) -> {
            target.setInt(offset, Short.toUnsignedInt(source.readShort()));
        };
    }

    static GeoReader<MutableShorts> copyBytesAsShorts(int n) {
        return (source, target, offset) -> {
            for (int i = 0; i < n; i++) {
                target.setShort(offset + i, (short) Byte.toUnsignedInt(source.readByte()));
            }
        };
    }

    static GeoReader<MutableFloats> copyBytesAsFloats(int n) {
        return (source, target, offset) -> {
            for (int i = 0; i < n; i++) {
                target.setFloat(offset + i, MathF.unpackUNorm8(source.readByte()));
            }
        };
    }

    static GeoReader<MutableBytes> copyBytes(int n) {
        return (source, target, offset) -> {
            for (int i = 0; i < n; i++) {
                target.setByte(offset + i, source.readByte());
            }
        };
    }

    private static Vector3 readVector3UNorm8Normal(BinaryReader reader) throws IOException {
        float x = MathF.unpackUNorm8Normal(reader.readByte());
        float y = MathF.unpackUNorm8Normal(reader.readByte());
        float z = MathF.unpackUNorm8Normal(reader.readByte());
        return new Vector3(x, y, z);
    }
}
