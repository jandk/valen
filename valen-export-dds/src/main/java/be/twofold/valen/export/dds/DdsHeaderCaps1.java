package be.twofold.valen.export.dds;

import java.util.*;

public enum DdsHeaderCaps1 {
    DDSCAPS_COMPLEX(0x8),
    DDSCAPS_TEXTURE(0x1000),
    DDSCAPS_MIPMAP(0x400000),
    ;

    private static final DdsHeaderCaps1[] VALUES = values();
    private final int value;

    DdsHeaderCaps1(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Set<DdsHeaderCaps1> fromValue(int value) {
        var result = EnumSet.noneOf(DdsHeaderCaps1.class);
        for (var flag : VALUES) {
            if ((value & flag.value) == flag.value) {
                result.add(flag);
                value &= ~flag.value;
            }
        }
        if (value != 0) {
            throw new IllegalArgumentException("Unknown DdsHeaderCaps1 value: " + value);
        }
        return result;
    }
}
