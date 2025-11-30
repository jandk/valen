package be.twofold.valen.format.granite.gtp;

import be.twofold.valen.core.util.*;

public enum Codec implements ValueEnum<Integer> {
    UNIFORM(0),
    COLOR_420(1),
    NORMAL(2),
    RAW_COLOR(3),
    BINARY(4),
    COLOR_420_15(5),
    NORMAL_15(6),
    RAW_NORMAL(7),
    HALF(8),
    BC(9),
    MULTI_CHANNEL(10),
    ASTC(11),
    ;
    private final int value;

    Codec(int value) {
        this.value = value;
    }

    public static Codec fromValue(int value) {
        return ValueEnum.fromValue(Codec.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
