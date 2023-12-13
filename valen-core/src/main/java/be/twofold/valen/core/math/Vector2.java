package be.twofold.valen.core.math;

import be.twofold.valen.core.util.*;

import java.nio.*;

public record Vector2(float x, float y) {

    public static Vector2 read(BetterBuffer buffer) {
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        return new Vector2(x, y);
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 subtract(Vector2 other) {
        return new Vector2(x - other.x, y - other.y);
    }

    public Vector2 multiply(float s) {
        return new Vector2(x * s, y * s);
    }

    public Vector2 divide(float s) {
        return new Vector2(x / s, y / s);
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public float lengthSquared() {
        return x * x + y * y;
    }

    public Vector2 normalize() {
        return divide(length());
    }

    // TODO: Move this method somewhere else
    public void put(FloatBuffer dst) {
        dst.put(x);
        dst.put(y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
