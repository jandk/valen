package be.twofold.valen.game.eternal.reader.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;

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

    public static Geo.Reader<FloatBuffer> readPosition(Vector3 offset, float scale) {
        return (source, dst) -> {
            dst.put(Math.fma(source.readFloat(), scale, offset.x()));
            dst.put(Math.fma(source.readFloat(), scale, offset.y()));
            dst.put(Math.fma(source.readFloat(), scale, offset.z()));
        };
    }

    public static Geo.Reader<FloatBuffer> readPackedPosition(Vector3 offset, float scale) {
        return (source, dst) -> {
            dst.put(Math.fma(MathF.unpackUNorm16(source.readShort()), scale, offset.x()));
            dst.put(Math.fma(MathF.unpackUNorm16(source.readShort()), scale, offset.y()));
            dst.put(Math.fma(MathF.unpackUNorm16(source.readShort()), scale, offset.z()));
            source.expectShort((short) 0);
        };
    }

    public static Geo.Reader<FloatBuffer> readPackedNormal() {
        return (source, dst) -> {
            float x = MathF.unpackUNorm8Normal(source.readByte());
            float y = MathF.unpackUNorm8Normal(source.readByte());
            float z = MathF.unpackUNorm8Normal(source.readByte());

            float scale = MathF.invSqrt(x * x + y * y + z * z);

            dst.put(x * scale);
            dst.put(y * scale);
            dst.put(z * scale);

            source.skip(5); // skip tangent
        };
    }

    public static Geo.Reader<FloatBuffer> readPackedTangent() {
        return (source, dst) -> {
            source.skip(4); // skip normal

            float x = MathF.unpackUNorm8Normal(source.readByte());
            float y = MathF.unpackUNorm8Normal(source.readByte());
            float z = MathF.unpackUNorm8Normal(source.readByte());
            float w = (source.readByte() & 0x80) == 0 ? 1 : -1;

            float scale = MathF.invSqrt(x * x + y * y + z * z);

            dst.put(x * scale);
            dst.put(y * scale);
            dst.put(z * scale);
            dst.put(w);
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

    public static Geo.Reader<FloatBuffer> readUV(Vector2 offset, float scale) {
        return (source, dst) -> {
            dst.put(Math.fma(source.readFloat(), scale, offset.x()));
            dst.put(Math.fma(source.readFloat(), scale, offset.y()));
        };
    }

    public static Geo.Reader<FloatBuffer> readPackedUV(Vector2 offset, float scale) {
        return (source, dst) -> {
            dst.put(Math.fma(MathF.unpackUNorm16(source.readShort()), scale, offset.x()));
            dst.put(Math.fma(MathF.unpackUNorm16(source.readShort()), scale, offset.y()));
        };
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
}
