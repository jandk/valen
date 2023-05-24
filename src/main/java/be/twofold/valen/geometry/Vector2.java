package be.twofold.valen.geometry;

public record Vector2(
    float x,
    float y
) {
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
