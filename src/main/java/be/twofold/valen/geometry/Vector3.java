package be.twofold.valen.geometry;

public record Vector3(
    float x,
    float y,
    float z
) {
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
