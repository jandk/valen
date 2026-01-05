package be.twofold.valen.core.util;

public final class MathF {
    private MathF() {
        throw new AssertionError();
    }

    // Standard math functions

    // Additional math functions

    public static float lerp(float a, float b, float t) {
        return Math.fma(t, b, Math.fma(-t, a, a));
    }

    public static float smoothstep(float t) {
        return t * t * Math.fma(-2.0f, t, 3.0f);
    }

    public static float smoothstep(float a, float b, float t) {
        return lerp(a, b, smoothstep(t));
    }

    public static float smootherstep(float t) {
        return t * t * t * Math.fma(t, Math.fma(t, 6.0f, -15.0f), 10.0f);
    }

    public static float smootherstep(float a, float b, float t) {
        return lerp(a, b, smootherstep(t));
    }


    public static float linearToSrgb(float f) {
        if (f <= (0.04045f / 12.92f)) {
            return f * 12.92f;
        } else {
            return Math.fma((float) Math.pow(f, 1.0f / 2.4f), 1.055f, -0.055f);
        }
    }

    public static float srgbToLinear(float f) {
        if (f <= 0.04045f) {
            return f * (1.0f / 12.92f);
        } else {
            return (float) Math.pow(Math.fma(f, 1.0f / 1.055f, 0.055f / 1.055f), 2.4f);
        }
    }

}
