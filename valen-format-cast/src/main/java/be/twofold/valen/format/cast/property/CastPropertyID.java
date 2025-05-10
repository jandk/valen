package be.twofold.valen.format.cast.property;

public enum CastPropertyID {
    BYTE(0x0062, 1, 1),
    SHORT(0x0068, 2, 1),
    INT(0x0069, 4, 1),
    LONG(0x006C, 8, 1),
    FLOAT(0x0066, 4, 1),
    DOUBLE(0x0064, 8, 1),
    STRING(0x0073, 0, 1),
    VECTOR2(0x7632, 8, 2),
    VECTOR3(0x7633, 12, 3),
    VECTOR4(0x7634, 16, 4),
    ;

    private final short id;
    private final int size;
    private final int count;

    CastPropertyID(int id, int size, int count) {
        this.id = (short) id;
        this.size = size;
        this.count = count;
    }

    public short id() {
        return id;
    }

    public int size() {
        return size;
    }

    public int count() {
        return count;
    }

    public static CastPropertyID fromValue(short value) {
        return switch (value) {
            case 0x0062 -> BYTE;
            case 0x0068 -> SHORT;
            case 0x0069 -> INT;
            case 0x006C -> LONG;
            case 0x0066 -> FLOAT;
            case 0x0064 -> DOUBLE;
            case 0x0073 -> STRING;
            case 0x7632 -> VECTOR2;
            case 0x7633 -> VECTOR3;
            case 0x7634 -> VECTOR4;
            default -> throw new IllegalArgumentException("Unknown CastPropertyID: " + value);
        };
    }
}
