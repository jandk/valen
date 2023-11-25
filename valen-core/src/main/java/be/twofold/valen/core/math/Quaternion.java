package be.twofold.valen.core.math;

import com.fasterxml.jackson.annotation.*;

import java.nio.*;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public record Quaternion(float x, float y, float z, float w) {
    public Quaternion add(Quaternion other) {
        return new Quaternion(x + other.x, y + other.y, z + other.z, w + other.w);
    }

    public Quaternion subtract(Quaternion other) {
        return new Quaternion(x - other.x, y - other.y, z - other.z, w - other.w);
    }

    public Quaternion multiply(Quaternion other) {
        return new Quaternion(
            w * other.x + x * other.w + y * other.z - z * other.y,
            w * other.y - x * other.z + y * other.w + z * other.x,
            w * other.z + x * other.y - y * other.x + z * other.w,
            w * other.w - x * other.x - y * other.y - z * other.z
        );
    }

    public float length() {
        return MathF.sqrt(lengthSquared());
    }

    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }

    public Quaternion normalize() {
        float length = length();
        return new Quaternion(
            x / length,
            y / length,
            z / length,
            w / length
        );
    }

    // TODO: Move this code somewhere else
    public void put(FloatBuffer buffer) {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        buffer.put(w);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
