package be.twofold.valen.export.dds;

import be.twofold.valen.core.util.*;

import java.util.*;

public enum DdsHeaderCaps1 implements FlagEnum {
    DDSCAPS_COMPLEX(0x8),
    DDSCAPS_TEXTURE(0x1000),
    DDSCAPS_MIPMAP(0x400000),
    ;

    private final int value;

    DdsHeaderCaps1(int value) {
        this.value = value;
    }

    public static Set<DdsHeaderCaps1> fromValue(int value) {
        return FlagEnum.fromValue(DdsHeaderCaps1.class, value);
    }

    @Override
    public int value() {
        return value;
    }
}
