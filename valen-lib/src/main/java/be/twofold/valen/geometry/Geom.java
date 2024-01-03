package be.twofold.valen.geometry;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

import java.util.function.*;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public final class Geom {
    private Geom() {
    }

    public static Function<BetterBuffer, Vector3> positionReader(int mask) {
        return switch (mask) {
            case 0x01 -> Geom::readPosition;
            case 0x20 -> Geom::readPackedPosition;
            default -> throw new IllegalArgumentException("Unknown position mask: " + mask);
        };
    }

    public static Function<BetterBuffer, Vector3> normalReader(int mask) {
        return switch (mask) {
            case 0x14 -> Geom::readPackedNormal;
            default -> throw new IllegalArgumentException("Unknown normal mask: " + mask);
        };
    }

    public static Function<BetterBuffer, Vector4> tangentReader(int mask) {
        return switch (mask) {
            case 0x14 -> Geom::readPackedTangent;
            default -> throw new IllegalArgumentException("Unknown tangent mask: " + mask);
        };
    }

    public static Function<BetterBuffer, Vector2> texCoordReader(int mask) {
        return switch (mask) {
            case 0x08000 -> Geom::readTexCoord;
            case 0x20000 -> Geom::readPackedTexCoord;
            default -> throw new IllegalArgumentException("Unknown texCoord mask: " + mask);
        };
    }

    private static Vector3 readPosition(BetterBuffer buffer) {
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        float z = buffer.getFloat();
        return new Vector3(x, y, z);
    }

    private static Vector3 readPackedPosition(BetterBuffer buffer) {
        float x = MathF.unpackUNorm16(buffer.getShort());
        float y = MathF.unpackUNorm16(buffer.getShort());
        float z = MathF.unpackUNorm16(buffer.getShort());
        buffer.skip(2);
        return new Vector3(x, y, z);
    }

    private static Vector3 readPackedNormal(BetterBuffer buffer) {
        float x = MathF.unpackSNorm8(buffer.getByte());
        float y = MathF.unpackSNorm8(buffer.getByte());
        float z = MathF.unpackSNorm8(buffer.getByte());
        buffer.skip(1);
        return new Vector3(x, y, z);
    }

    private static Vector4 readPackedTangent(BetterBuffer buffer) {
        float x = MathF.unpackSNorm8(buffer.getByte());
        float y = MathF.unpackSNorm8(buffer.getByte());
        float z = MathF.unpackSNorm8(buffer.getByte());
        // Branch-less version of: float w = (buffer.getByte() & 0x80) != 0 ? 1.0f : -1.0f;
        float w = Float.intBitsToFloat((buffer.getByte() & 0x80) << 24 | 0x3f800000);
        return new Vector4(x, y, z, w);
    }

    private static Vector2 readTexCoord(BetterBuffer buffer) {
        float u = buffer.getFloat();
        float v = buffer.getFloat();
        return new Vector2(u, v);
    }

    private static Vector2 readPackedTexCoord(BetterBuffer buffer) {
        float u = MathF.unpackUNorm16(buffer.getShort());
        float v = MathF.unpackUNorm16(buffer.getShort());
        return new Vector2(u, v);
    }

}
