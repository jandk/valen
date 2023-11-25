package be.twofold.valen.core.math;

public final class MathF {
    private MathF() {
        throw new AssertionError();
    }

    public static float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    public static float saturate(float value) {
        return clamp(value, 0.0f, 1.0f);
    }

    public static float sqrt(float a) {
        return (float) Math.sqrt(a);
    }
}