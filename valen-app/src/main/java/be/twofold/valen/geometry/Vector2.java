package be.twofold.valen.geometry;

import com.fasterxml.jackson.annotation.*;

import java.nio.*;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public record Vector2(
    float x,
    float y
) {
    public Vector2 add(Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }

    public Vector2 mul(float s) {
        return new Vector2(x * s, y * s);
    }

    public void put(FloatBuffer dst) {
        dst.put(x);
        dst.put(y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
