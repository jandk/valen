package be.twofold.valen.core.math;

import java.nio.*;

public record Vector3(
    float x,
    float y,
    float z
) {
    public static final Vector3 Zero = new Vector3(0.0f, 0.0f, 0.0f);
    public static final Vector3 One = new Vector3(1.0f, 1.0f, 1.0f);
    public static final Vector3 UnitX = new Vector3(1.0f, 0.0f, 0.0f);
    public static final Vector3 UnitY = new Vector3(0.0f, 1.0f, 0.0f);
    public static final Vector3 UnitZ = new Vector3(0.0f, 0.0f, 1.0f);

    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return add(other.negate());
    }

    public Vector3 multiply(float scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
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

    // TODO: Move this method somewhere else
    public void put(FloatBuffer dst) {
        dst.put(x);
        dst.put(y);
        dst.put(z);
    }

    // TODO: Move this method somewhere else
    public float[] toArray() {
        return new float[]{x, y, z};
    }

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
