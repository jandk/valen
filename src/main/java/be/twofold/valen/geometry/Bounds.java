package be.twofold.valen.geometry;

public record Bounds(
    Vector3 min,
    Vector3 max
) {
    @Override
    public String toString() {
        return "(" + min + ", " + max + ")";
    }
}
