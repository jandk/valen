package be.twofold.valen.core.math;

public final class MathF {
    private MathF() {
        throw new AssertionError();
    }

    public static float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    public static float clamp01(float value) {
        return clamp(value, 0.0f, 1.0f);
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float sqrt(float a) {
        return (float) Math.sqrt(a);
    }

    public static float unpackSNorm8(byte value) {
        return clamp(value * (1.0f / 127.0f), -1.0f, 1.0f);
    }

    public static float unpackUNorm8(byte value) {
        return Byte.toUnsignedInt(value) * (1.0f / 255.0f);
    }

    public static float unpackUNorm16(short value) {
        return Short.toUnsignedInt(value) * (1.0f / 65535.0f);
    }
}
