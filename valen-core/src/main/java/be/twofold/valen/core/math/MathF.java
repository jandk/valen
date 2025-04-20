package be.twofold.valen.core.math;

public final class MathF {
    public static final float SQRT_2 = 1.4142135f;
    public static final float SQRT1_2 = 0.70710677f;

    public static final float PI = (float) Math.PI;
    public static final float TAU = (float) Math.TAU;
    public static final float HALF_PI = (float) (Math.PI / 2.0);

    private MathF() {
        throw new AssertionError();
    }

    // Standard math functions

    public static float sin(float angle) {
        return (float) Math.sin(angle);
    }

    public static float cos(float angle) {
        return (float) Math.cos(angle);
    }

    public static float sqrt(float a) {
        return (float) Math.sqrt(a);
    }

    public static float pow(float a, float b) {
        return (float) Math.pow(a, b);
    }

    public static float clamp(float value, float min, float max) {
        assert min <= max : "min > max";
        return Math.min(max, Math.max(value, min));
    }

    // equals and hashCode

    public static boolean equals(float a, float b) {
        return Float.isNaN(a) ? Float.isNaN(b) : a == b;
    }

    public static int hashCode(float value) {
        return Float.isNaN(value)
            ? 0x7fc00000
            : Float.floatToRawIntBits(value + 0.0f);
    }

    // Additional math functions

    public static float clamp01(float value) {
        return clamp(value, 0.0f, 1.0f);
    }

    public static float clamp11(float value) {
        return clamp(value, -1.0f, 1.0f);
    }

    public static float invSqrt(float a) {
        return 1.0f / sqrt(a);
    }

    public static float lerp(float a, float b, float t) {
        return Math.fma(t, b - a, a);
    }

    public static byte packUNorm8Normal(float value) {
        return packUNorm8(Math.fma(value, 0.5f, 0.5f));
    }

    public static float unpackUNorm8Normal(byte value) {
        return Math.fma(unpackUNorm8(value), 2.0f, -1.0f);
    }

    public static byte packSNorm8(float value) {
        return (byte) Math.round(clamp11(value) * Byte.MAX_VALUE);
    }

    public static short packSNorm16(float value) {
        return (short) Math.round(clamp11(value) * Short.MAX_VALUE);
    }

    public static byte packUNorm8(float value) {
        return (byte) Math.fma(clamp01(value), 255.0f, 0.5f);
    }

    public static short packUNorm16(float value) {
        return (short) Math.fma(clamp01(value), 65535.0f, 0.5f);
    }

    public static float unpackSNorm8(byte value) {
        return Math.max(-Byte.MAX_VALUE, value) * (1.0f / Byte.MAX_VALUE);
    }

    public static float unpackSNorm16(short value) {
        return Math.max(-Short.MAX_VALUE, value) * (1.0f / Short.MAX_VALUE);
    }

    public static float unpackUNorm8(byte value) {
        return Byte.toUnsignedInt(value) * (1.0f / 255.0f);
    }

    public static float unpackUNorm16(short value) {
        return Short.toUnsignedInt(value) * (1.0f / 65535.0f);
    }

    public static float linearToSrgb(float f) {
        if (f <= (0.04045f / 12.92f)) {
            return f * 12.92f;
        } else {
            return Math.fma(pow(f, 1.0f / 2.4f), 1.055f, -0.055f);
        }
    }

    public static float srgbToLinear(float f) {
        if (f <= 0.04045f) {
            return f * (1.0f / 12.92f);
        } else {
            return pow(Math.fma(f, 1.0f / 1.055f, 0.055f / 1.055f), 2.4f);
        }
    }

    public static float toDegrees(float angle) {
        return (float) Math.toDegrees(angle);
    }

    public static float toRadians(float angle) {
        return (float) Math.toRadians(angle);
    }
}
