package be.twofold.valen.core.math;

import java.nio.*;

public record Vector4(float x, float y, float z, float w) {
    public Vector4 add(Vector4 other) {
        return new Vector4(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public Vector4 subtract(Vector4 other) {
        return new Vector4(x - other.x, y - other.y, z - other.z, w - other.w);
    }

    public Vector4 multiply(float s) {
        return new Vector4(x * s, y * s, z * s, w * s);
    }

    public Vector4 divide(float s) {
        return new Vector4(x / s, y / s, z / s, w / s);
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    public Vector4 normalize() {
        return divide(length());
    }

    // TODO: Move this method somewhere else
    public void put(FloatBuffer dst) {
        dst.put(x);
        dst.put(y);
        dst.put(z);
        dst.put(w);
    }

    // TODO: Move this method somewhere else
    public float[] toArray() {
        return new float[]{x, y, z, w};
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
