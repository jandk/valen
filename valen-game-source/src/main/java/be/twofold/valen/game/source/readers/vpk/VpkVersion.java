package be.twofold.valen.game.source.readers.vpk;

public enum VpkVersion {
    ONE(1, 12),
    TWO(2, 28),
    ;

    private final int value;
    private final int size;

    VpkVersion(int value, int size) {
        this.value = value;
        this.size = size;
    }

    public int size() {
        return size;
    }

    public static VpkVersion fromValue(int value) {
        for (var version : values()) {
            if (version.value == value) {
                return version;
            }
        }
        throw new UnsupportedOperationException("Unknown VPK version: " + value);
    }
}
