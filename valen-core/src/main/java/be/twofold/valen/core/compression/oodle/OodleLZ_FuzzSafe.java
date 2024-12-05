package be.twofold.valen.core.compression.oodle;

public enum OodleLZ_FuzzSafe {
    No(0),
    Yes(1);

    private final int value;

    OodleLZ_FuzzSafe(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
