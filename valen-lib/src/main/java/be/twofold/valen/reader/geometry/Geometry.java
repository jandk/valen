package be.twofold.valen.reader.geometry;

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

    public static void readVertex(DataSource source, FloatBuffer dst, Vector3 offset, float scale) throws IOException {
        dst.put(Math.fma(source.readFloat(), scale, offset.x()));
        dst.put(Math.fma(source.readFloat(), scale, offset.y()));
        dst.put(Math.fma(source.readFloat(), scale, offset.z()));
    }

    public static void readPackedVertex(DataSource source, FloatBuffer dst, Vector3 offset, float scale) throws IOException {
        dst.put(Math.fma(MathF.unpackUNorm16(source.readShort()), scale, offset.x()));
        dst.put(Math.fma(MathF.unpackUNorm16(source.readShort()), scale, offset.y()));
        dst.put(Math.fma(MathF.unpackUNorm16(source.readShort()), scale, offset.z()));
        source.expectShort((short) 0);
    }

    public static void readPackedNormal(DataSource source, FloatBuffer dst) throws IOException {
        float x = MathF.unpack8(source.readByte());
        float y = MathF.unpack8(source.readByte());
        float z = MathF.unpack8(source.readByte());

        float scale = 1.0f / MathF.sqrt(x * x + y * y + z * z);

        dst.put(x * scale);
        dst.put(y * scale);
        dst.put(z * scale);

        source.skip(5); // skip tangent
    }

    public static void readPackedTangent(DataSource source, FloatBuffer dst) throws IOException {
        source.skip(4); // skip normal

        float x = MathF.unpack8(source.readByte());
        float y = MathF.unpack8(source.readByte());
        float z = MathF.unpack8(source.readByte());
        float w = (source.readByte() & 0x80) == 0 ? 1 : -1;

        float scale = 1.0f / MathF.sqrt(x * x + y * y + z * z);

        dst.put(x * scale);
        dst.put(y * scale);
        dst.put(z * scale);
        dst.put(w);
    }

    public static void readWeight(DataSource source, ByteBuffer dst) throws IOException {
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
    }

    public static void readUV(DataSource source, FloatBuffer dst, Vector2 offset, float scale) throws IOException {
        dst.put(Math.fma(source.readFloat(), scale, offset.x()));
        dst.put(Math.fma(source.readFloat(), scale, offset.y()));
    }

    public static void readPackedUV(DataSource source, FloatBuffer dst, Vector2 offset, float scale) throws IOException {
        dst.put(Math.fma(MathF.unpackUNorm16(source.readShort()), scale, offset.x()));
        dst.put(Math.fma(MathF.unpackUNorm16(source.readShort()), scale, offset.y()));
    }
}
