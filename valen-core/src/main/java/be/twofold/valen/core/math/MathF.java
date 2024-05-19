package be.twofold.valen.core.math;

public final class MathF {
    public static final float SQRT_2 = 1.4142135f;
    public static final float SQRT1_2 = 0.70710677f;

    private MathF() {
        throw new AssertionError();
    }

    public static float clamp01(float value) {
        return Math.clamp(value, 0.0f, 1.0f);
    }

    public static float lerp(float a, float b, float t) {
        return Math.fma(t, b - a, a);
    }

    public static float sqrt(float a) {
        return (float) Math.sqrt(a);
    }

    public static float unpackUNorm8Normal(byte value) {
        return Math.fma(unpackUNorm8(value), 2.0f, -1.0f);
    }

    public static byte packUNorm8Normal(float value) {
        return packUNorm8(Math.fma(value, 0.5f, 0.5f));
    }

    public static float unpackUNorm8(byte value) {
        return Byte.toUnsignedInt(value) * (1.0f / 255.0f);
    }

    public static float unpackUNorm16(short value) {
        return Short.toUnsignedInt(value) * (1.0f / 65535.0f);
    }

    public static byte packUNorm8(float value) {
        return (byte) Math.round(clamp01(value) * 255.0f);
    }
}
