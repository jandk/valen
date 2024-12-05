package be.twofold.valen.core.compression.oodle;

public enum OodleLZ_Verbosity {
    None(0),
    Minimal(1),
    Some(2),
    Lots(3);

    private final int value;

    OodleLZ_Verbosity(int nativeValue) {
        this.value = nativeValue;
    }

    public int value() {
        return value;
    }
}
