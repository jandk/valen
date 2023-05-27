package be.twofold.valen.geometry;

import java.nio.*;

public record Vector3(
    float x,
    float y,
    float z
) {
    public Vector3 add(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 mul(float s) {
        return new Vector3(x * s, y * s, z * s);
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 normalize() {
        return mul(1 / length());
    }

    public void put(FloatBuffer dst) {
        dst.put(x);
        dst.put(y);
        dst.put(z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public float[] toArray() {
        return new float[]{x, y, z};
    }
}
