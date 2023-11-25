package be.twofold.valen.core.math;

import com.fasterxml.jackson.annotation.*;

import java.nio.*;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public record Vector2(float x, float y) {
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
