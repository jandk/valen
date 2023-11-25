package be.twofold.valen.geometry;

import com.fasterxml.jackson.annotation.*;

import java.nio.*;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public record Vector4(
    float x,
    float y,
    float z,
    float w
) {
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }

    public void put(FloatBuffer buffer) {
        buffer.put(x);
        buffer.put(y);
        buffer.put(z);
        buffer.put(w);
    }
}
