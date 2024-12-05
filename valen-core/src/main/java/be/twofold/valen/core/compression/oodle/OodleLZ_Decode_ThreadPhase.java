package be.twofold.valen.core.compression.oodle;

public enum OodleLZ_Decode_ThreadPhase {
    One(1),
    Two(2),
    All(3);

    private final int value;

    OodleLZ_Decode_ThreadPhase(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
