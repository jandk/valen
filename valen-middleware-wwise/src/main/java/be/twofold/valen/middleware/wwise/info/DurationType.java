package be.twofold.valen.middleware.wwise.info;

public enum DurationType {
    INFINITE,
    MIXED,
    ONE_SHOT,
    UNKNOWN;

    static DurationType fromString(String value) {
        return switch (value) {
            case "Infinite" -> INFINITE;
            case "Mixed" -> MIXED;
            case "OneShot" -> ONE_SHOT;
            case "Unknown" -> UNKNOWN;
            default -> throw new IllegalArgumentException("Unknown duration type: " + value);
        };
    }
}
