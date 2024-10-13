package be.twofold.valen.core.math;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;

public record Vector2(
    float x,
    float y
) {
    public static Vector2 Zero = new Vector2(0.0f, 0.0f);
    public static Vector2 One = new Vector2(1.0f, 1.0f);
    public static Vector2 X = new Vector2(1.0f, 0.0f);
    public static Vector2 Y = new Vector2(0.0f, 1.0f);

    public static Vector2 splat(float value) {
        return new Vector2(value, value);
    }

    public static Vector2 read(DataSource source) throws IOException {
        float x = source.readFloat();
        float y = source.readFloat();
        return new Vector2(x, y);
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 subtract(Vector2 other) {
        return add(other.negate());
    }

    public Vector2 multiply(Vector2 other) {
        return new Vector2(x * other.x, y * other.y);
    }

    public Vector2 multiply(float scalar) {
        return new Vector2(x * scalar, y * scalar);
    }

    public Vector2 divide(Vector2 other) {
        return new Vector2(x / other.x, y / other.y);
    }

    public Vector2 divide(float scalar) {
        return multiply(1.0f / scalar);
    }

    public Vector2 negate() {
        return new Vector2(-x, -y);
    }

    public float dot(Vector2 other) {
        return x * other.x + y * other.y;
    }

    public float lengthSquared() {
        return dot(this);
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public Vector2 normalize() {
        return divide(length());
    }

    public Vector2 fma(Vector2 scale, Vector2 offset) {
        float x = Math.fma(this.x, scale.x, offset.x);
        float y = Math.fma(this.y, scale.y, offset.y);
        return new Vector2(x, y);
    }

    public Vector2 fma(float scale, Vector2 offset) {
        return fma(splat(scale), offset);
    }

    public void toBuffer(FloatBuffer buffer) {
        buffer.put(x);
        buffer.put(y);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Vector2 other
            && MathF.equals(x, other.x)
            && MathF.equals(y, other.y);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + MathF.hashCode(x);
        result = 31 * result + MathF.hashCode(y);
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
