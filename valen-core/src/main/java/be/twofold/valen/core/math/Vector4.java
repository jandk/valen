package be.twofold.valen.core.math;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;

public record Vector4(
    float x,
    float y,
    float z,
    float w
) {
    public static Vector4 Zero = new Vector4(0.0f, 0.0f, 0.0f, 0.0f);
    public static Vector4 One = new Vector4(1.0f, 1.0f, 1.0f, 1.0f);
    public static Vector4 X = new Vector4(1.0f, 0.0f, 0.0f, 0.0f);
    public static Vector4 Y = new Vector4(0.0f, 1.0f, 0.0f, 0.0f);
    public static Vector4 Z = new Vector4(0.0f, 0.0f, 1.0f, 0.0f);
    public static Vector4 W = new Vector4(0.0f, 0.0f, 0.0f, 1.0f);

    public static Vector4 splat(float value) {
        return new Vector4(value, value, value, value);
    }

    public static Vector4 read(DataSource source) throws IOException {
        float x = source.readFloat();
        float y = source.readFloat();
        float z = source.readFloat();
        float w = source.readFloat();
        return new Vector4(x, y, z, w);
    }

    public Vector4(Vector3 v, float w) {
        this(v.x(), v.y(), v.z(), w);
    }

    public Vector4 add(Vector4 other) {
        return new Vector4(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public Vector4 subtract(Vector4 other) {
        return add(other.negate());
    }

    public Vector4 multiply(Vector4 other) {
        return new Vector4(x * other.x, y * other.y, z * other.z, w * other.w);
    }

    public Vector4 multiply(float scalar) {
        return new Vector4(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public Vector4 divide(Vector4 other) {
        return new Vector4(x / other.x, y / other.y, z / other.z, w / other.w);
    }

    public Vector4 divide(float scalar) {
        return multiply(1.0f / scalar);
    }

    public Vector4 negate() {
        return new Vector4(-x, -y, -z, -w);
    }

    public float dot(Vector4 other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public float lengthSquared() {
        return dot(this);
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public Vector4 normalize() {
        return divide(length());
    }

    public Vector4 fma(float scale, Vector4 offset) {
        return fma(splat(scale), offset);
    }

    public Vector4 fma(Vector4 scale, Vector4 offset) {
        float x = Math.fma(this.x, scale.x, offset.x);
        float y = Math.fma(this.y, scale.y, offset.y);
        float z = Math.fma(this.z, scale.z, offset.z);
        float w = Math.fma(this.w, scale.w, offset.w);
        return new Vector4(x, y, z, w);
    }

    public void toBuffer(FloatBuffer dst) {
        dst.put(x);
        dst.put(y);
        dst.put(z);
        dst.put(w);
    }

    public Vector3 toVector3() {
        return new Vector3(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Vector4 other
            && MathF.equals(x, other.x)
            && MathF.equals(y, other.y)
            && MathF.equals(z, other.z)
            && MathF.equals(w, other.w);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + MathF.hashCode(x);
        result = 31 * result + MathF.hashCode(y);
        result = 31 * result + MathF.hashCode(z);
        result = 31 * result + MathF.hashCode(w);
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
