package be.twofold.valen.geometry;

import com.fasterxml.jackson.annotation.*;

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
}
