package be.twofold.valen.core.util;

import wtf.reversed.toolbox.math.*;

// TODO: Either migrate this stuff or decide to put it in toolbox
public final class MathF {
    private MathF() {
    }

    // Additional math functions

    public static float clamp01(float value) {
        return Math.clamp(value, 0.0f, 1.0f);
    }

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

    public static float unpackUNorm8Normal(byte value) {
        return Math.fma(FloatMath.unpackUNorm8(value), 2.0f, -1.0f);
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
            float a = Math.fma(f, 1.0f / 1.055f, 0.055f / 1.055f);
            return (float) Math.pow(a, 2.4f);
        }
    }
}
