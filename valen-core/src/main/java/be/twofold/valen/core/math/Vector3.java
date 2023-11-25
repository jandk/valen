package be.twofold.valen.core.math;

import com.fasterxml.jackson.annotation.*;

import java.nio.*;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public record Vector3(float x, float y, float z) {
    public Vector3 add(Vector3 other) {
        return new Vector3(x + other.x, y + other.y, z + other.z);
    }

    public Vector3 subtract(Vector3 other) {
        return new Vector3(x - other.x, y - other.y, z - other.z);
    }

    public Vector3 multiply(float s) {
        return new Vector3(x * s, y * s, z * s);
    }

    public Vector3 divide(float s) {
        return new Vector3(x / s, y / s, z / s);
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public float lengthSquared() {
        return x * x + y * y + z * z;
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
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
