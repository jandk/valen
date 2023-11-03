package be.twofold.valen.geometry;

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
