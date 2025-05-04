package be.twofold.valen.format.cast;

public enum CastPropertyID {
    BYTE((short) 'b', Byte.BYTES),
    SHORT((short) 'h', Short.BYTES),
    INT((short) 'i', Integer.BYTES),
    LONG((short) 'l', Long.BYTES),
    FLOAT((short) 'f', Float.BYTES),
    DOUBLE((short) 'd', Double.BYTES),
    STRING((short) 's', 0),
    VECTOR2((short) ('v' << 8 | '2'), Vec2.BYTES),
    VECTOR3((short) ('v' << 8 | '3'), Vec3.BYTES),
    VECTOR4((short) ('v' << 8 | '4'), Vec4.BYTES),
    ;

    private final short value;
    private final int elementSize;

    CastPropertyID(short value, int elementSize) {
        this.value = value;
        this.elementSize = elementSize;
    }

    public short value() {
        return value;
    }

    public int elementSize() {
        return elementSize;
    }

    public static CastPropertyID fromValue(short value) {
        return switch (value) {
            case 'b' -> BYTE;
            case 'h' -> SHORT;
            case 'i' -> INT;
            case 'l' -> LONG;
            case 'f' -> FLOAT;
            case 'd' -> DOUBLE;
            case 's' -> STRING;
            case ('v' << 8 | '2') -> VECTOR2;
            case ('v' << 8 | '3') -> VECTOR3;
            case ('v' << 8 | '4') -> VECTOR4;
            default -> throw new IllegalArgumentException("Unknown CastPropertyID: " + value);
        };
    }
}
