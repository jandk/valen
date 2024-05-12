package be.twofold.valen.geometry;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public final class Geom {
    private Geom() {
    }

    public static StructMapper<Vector3> positionReader(int mask) {
        return switch (mask) {
            case 0x01 -> Geom::readPosition;
            case 0x20 -> Geom::readPackedPosition;
            default -> throw new IllegalArgumentException("Unknown position mask: " + mask);
        };
    }

    public static StructMapper<Vector3> normalReader(int mask) {
        return switch (mask) {
            case 0x14 -> Geom::readPackedNormal;
            default -> throw new IllegalArgumentException("Unknown normal mask: " + mask);
        };
    }

    public static StructMapper<Vector4> tangentReader(int mask) {
        return switch (mask) {
            case 0x14 -> Geom::readPackedTangent;
            default -> throw new IllegalArgumentException("Unknown tangent mask: " + mask);
        };
    }

    public static StructMapper<Vector2> texCoordReader(int mask) {
        return switch (mask) {
            case 0x08000 -> Geom::readTexCoord;
            case 0x20000 -> Geom::readPackedTexCoord;
            default -> throw new IllegalArgumentException("Unknown texCoord mask: " + mask);
        };
    }

    private static Vector3 readPosition(DataSource source) throws IOException {
        float x = source.readFloat();
        float y = source.readFloat();
        float z = source.readFloat();
        return new Vector3(x, y, z);
    }

    private static Vector3 readPackedPosition(DataSource source) throws IOException {
        float x = MathF.unpackUNorm16(source.readShort());
        float y = MathF.unpackUNorm16(source.readShort());
        float z = MathF.unpackUNorm16(source.readShort());
        source.skip(2);
        return new Vector3(x, y, z);
    }

    private static Vector3 readPackedNormal(DataSource source) throws IOException {
        float x = MathF.unpack8(source.readByte());
        float y = MathF.unpack8(source.readByte());
        float z = MathF.unpack8(source.readByte());
        source.skip(1);
        return new Vector3(x, y, z);
    }

    private static Vector4 readPackedTangent(DataSource source) throws IOException {
        float x = MathF.unpack8(source.readByte());
        float y = MathF.unpack8(source.readByte());
        float z = MathF.unpack8(source.readByte());
        // Branch-less version of: float w = (source.readByte() & 0x80) != 0 ? 1.0f : -1.0f;
        float w = Float.intBitsToFloat((source.readByte() & 0x80) << 24 | 0x3f800000);
        return new Vector4(x, y, z, w);
    }

    private static Vector2 readTexCoord(DataSource source) throws IOException {
        float u = source.readFloat();
        float v = source.readFloat();
        return new Vector2(u, v);
    }

    private static Vector2 readPackedTexCoord(DataSource source) throws IOException {
        float u = MathF.unpackUNorm16(source.readShort());
        float v = MathF.unpackUNorm16(source.readShort());
        return new Vector2(u, v);
    }

}
