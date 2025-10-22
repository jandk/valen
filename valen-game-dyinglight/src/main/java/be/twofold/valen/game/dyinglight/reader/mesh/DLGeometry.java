package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;

import java.nio.*;

public final class DLGeometry {
    public static GeoReader<FloatBuffer> readPosition() {
        return (source, dst) -> Vector3.read(source).toBuffer(dst);
    }

    public static GeoReader<ShortBuffer> readFace() {
        return copyShorts(1);
    }

    public static GeoReader<ShortBuffer> readJoints(Shorts shorts) {
        return (source, dst) -> {
            int b0 = Byte.toUnsignedInt(source.readByte());
            short j0 = b0 >= shorts.size() ? 0 : shorts.getShort(b0);
            int b1 = Byte.toUnsignedInt(source.readByte());
            short j1 = b1 >= shorts.size() ? 0 : shorts.getShort(b1);
            int b2 = Byte.toUnsignedInt(source.readByte());
            short j2 = b2 >= shorts.size() ? 0 : shorts.getShort(b2);
            int b3 = Byte.toUnsignedInt(source.readByte());
            short j3 = b3 >= shorts.size() ? 0 : shorts.getShort(b3);
            dst.put(j0).put(j1).put(j2).put(j3);
        };
    }

    public static GeoReader<FloatBuffer> readPackedI16Normals() {
        return (source, dst) -> {
            float x = MathF.unpackSNorm16(source.readShort());
            float y = MathF.unpackSNorm16(source.readShort());
            float z = MathF.unpackSNorm16(source.readShort());
            float w = MathF.unpackSNorm16(source.readShort());
            var normalQuat = new Quaternion(x, y, z, -w);
            var identityQuat = new Quaternion(0, 0, 1, 0);
            var transformed = normalQuat.invert().multiply(identityQuat);
            transformed = transformed.multiply(normalQuat);
            dst.put(transformed.x()).put(transformed.y()).put(transformed.z());
        };
    }

    public static GeoReader<FloatBuffer> readHalfVector3() {
        return (source, dst) -> {
            float x = Float.float16ToFloat(source.readShort());
            float y = Float.float16ToFloat(source.readShort());
            float z = Float.float16ToFloat(source.readShort());
            dst.put(x).put(y).put(z);
        };
    }

    public static GeoReader<FloatBuffer> readByteNormal() {
        return (source, dst) -> {
            float x = MathF.unpackSNorm8(source.readByte());
            float y = MathF.unpackSNorm8(source.readByte());
            float z = MathF.unpackSNorm8(source.readByte());
            float w = MathF.unpackSNorm8(source.readByte());
            var normalQuat = new Quaternion(x, y, z, -w);
            var identityQuat = new Quaternion(0, 0, 1, 0);
            var transformed = normalQuat.invert().multiply(identityQuat);
            transformed = transformed.multiply(normalQuat);
            dst.put(transformed.x()).put(transformed.y()).put(transformed.z());
        };
    }

    public static GeoReader<FloatBuffer> readHalfVector2() {
        return (source, dst) -> {
            float u = Float.float16ToFloat(source.readShort());
            float v = Float.float16ToFloat(source.readShort());
            dst.put(u).put(v);
        };
    }

    public static GeoReader<FloatBuffer> readWeights() {
        return (source, dst) -> {
            float w1 = MathF.unpackUNorm8(source.readByte());
            float w2 = MathF.unpackUNorm8(source.readByte());
            float w3 = MathF.unpackUNorm8(source.readByte());
            float w4 = MathF.unpackUNorm8(source.readByte());
            float sum = w1 + w2 + w3 + w4;
            if (Math.abs(1.f - sum) > 0.01f) {
                throw new IllegalStateException("Weights do not sum to 1: " + w1 + "+" + w2 + "+" + w3 + "+" + w4 + "=" + sum);
            }
            dst.put(w1).put(w2).put(w3).put(w4);
        };
    }

    public static GeoReader<ShortBuffer> copyShorts(int n) {
        return (source, dst) -> {
            for (int i = 0; i < n; i++) {
                dst.put(source.readShort());
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
}
