package be.twofold.valen.format.cast;

public record Vec4(
    float x,
    float y,
    float z,
    float w
) {
    public static final int BYTES = 4 * Float.BYTES;

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
