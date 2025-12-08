package be.twofold.valen.format.granite.enums;

import be.twofold.valen.core.util.*;

public enum Format implements ValueEnum<Integer> {
    R8G8B8A8(0),
    R16G16B16A16(1),
    R32G32B32A32(2),
    BC1(3),
    BC3(4),
    BC5(5),
    BC7(6),
    BC6(7),
    BC4(8),
    ASTC4X4(9),
    ASTC8X8(10),
    R32(11),
    R32G32(12),
    R32G32B32(13),
    ;

    private final int value;

    Format(int value) {
        this.value = value;
    }

    public static Format fromValue(int value) {
        return ValueEnum.fromValue(Format.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
