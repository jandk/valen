package be.twofold.valen.core.math;

import java.nio.*;

public record Quaternion(float x, float y, float z, float w) {
    public static final Quaternion Identity = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);

    public static Quaternion fromAxisAngle(Vector3 axis, float angle) {
        float halfAngle = angle * 0.5f;
        float sin = MathF.sin(halfAngle);
        float cos = MathF.cos(halfAngle);
        float x = axis.x() * sin;
        float y = axis.y() * sin;
        float z = axis.z() * sin;
        return new Quaternion(x, y, z, cos);
    }

    public Quaternion add(Quaternion other) {
        return new Quaternion(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public Quaternion subtract(Quaternion other) {
        return add(other.negate());
    }

    public Quaternion multiply(float scalar) {
        return new Quaternion(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public Quaternion divide(float scalar) {
        return multiply(1.0f / scalar);
    }

    public Quaternion negate() {
        return new Quaternion(-x, -y, -z, -w);
    }

    public float dot(Quaternion other) {
        return x * other.x + y * other.y + z * other.z + w * other.w;
    }

    public float lengthSquared() {
        return dot(this);
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public Quaternion normalize() {
        return divide(length());
    }

    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
            w * other.x + x * other.w + y * other.z - z * other.y,
            w * other.y - x * other.z + y * other.w + z * other.x,
            w * other.z + x * other.y - y * other.x + z * other.w,
            w * other.w - x * other.x - y * other.y - z * other.z
        );
    }

    // TODO: Move this method somewhere else
    public void put(FloatBuffer buffer) {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        buffer.put(w);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof Quaternion other
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
