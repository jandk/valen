package be.twofold.valen.core.math;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;

public record Vector3(
    float x,
    float y,
    float z
) {
    public static final Vector3 Zero = new Vector3(0.0f, 0.0f, 0.0f);
    public static final Vector3 One = new Vector3(1.0f, 1.0f, 1.0f);
    public static final Vector3 X = new Vector3(1.0f, 0.0f, 0.0f);
    public static final Vector3 Y = new Vector3(0.0f, 1.0f, 0.0f);
    public static final Vector3 Z = new Vector3(0.0f, 0.0f, 1.0f);

    public static Vector3 splat(float value) {
        return new Vector3(value, value, value);
    }

    public static Vector3 read(BinaryReader reader) throws IOException {
        float x = reader.readFloat();
        float y = reader.readFloat();
        float z = reader.readFloat();
        return new Vector3(x, y, z);
    }

    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return add(other.negate());
    }

    public Vector3 multiply(Vector3 other) {
        return new Vector3(x * other.x, y * other.y, z * other.z);
    }

    public Vector3 multiply(float scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }

    public Vector3 divide(Vector3 other) {
        return new Vector3(x / other.x, y / other.y, z / other.z);
    }

    public Vector3 divide(float scalar) {
        return multiply(1.0f / scalar);
    }

    public Vector3 negate() {
        return new Vector3(-x, -y, -z);
    }

    public float dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public float lengthSquared() {
        return dot(this);
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public Vector3 normalize() {
        return divide(length());
    }

    // Custom methods

    public Vector3 cross(Vector3 other) {
        return new Vector3(
            y * other.z - other.y * z,
            z * other.x - other.z * x,
            x * other.y - other.x * y
        );
    }

    public Vector3 fma(float scale, Vector3 offset) {
        return fma(splat(scale), offset);
    }

    public Vector3 fma(Vector3 scale, Vector3 offset) {
        float x = Math.fma(this.x, scale.x, offset.x);
        float y = Math.fma(this.y, scale.y, offset.y);
        float z = Math.fma(this.z, scale.z, offset.z);
        return new Vector3(x, y, z);
    }

    public void toBuffer(FloatBuffer buffer) {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
    }

    public Vector3 map(FloatUnaryOperator operator) {
        var x = operator.applyAsFloat(this.x);
        var y = operator.applyAsFloat(this.y);
        var z = operator.applyAsFloat(this.z);
        return new Vector3(x, y, z);
    }

    // Object methods

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Vector3 other
            && MathF.equals(x, other.x)
            && MathF.equals(y, other.y)
            && MathF.equals(z, other.z);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + MathF.hashCode(x);
        result = 31 * result + MathF.hashCode(y);
        result = 31 * result + MathF.hashCode(z);
        return result;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
