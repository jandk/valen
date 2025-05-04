package be.twofold.valen.format.cast;

public record Vec3(
    float x,
    float y,
    float z
) {
    public static final int BYTES = 3 * Float.BYTES;

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
