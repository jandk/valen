package be.twofold.valen.format.cast;

public record Vec2(
    float x,
    float y
) {
    public static final int BYTES = 2 * Float.BYTES;

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
