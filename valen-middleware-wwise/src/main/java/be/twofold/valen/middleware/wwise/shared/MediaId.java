package be.twofold.valen.middleware.wwise.shared;

public record MediaId(
    int value
) {
    public static MediaId of(int value) {
        return new MediaId(value);
    }

    public static MediaId parse(String value) {
        return new MediaId(Integer.parseUnsignedInt(value));
    }

    @Override
    public String toString() {
        return Integer.toUnsignedString(value);
    }
}
