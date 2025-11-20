package be.twofold.valen.export.dds;

import java.util.*;

public enum DdsHeaderCaps2 {
    DDSCAPS2_CUBEMAP(0x200),
    DDSCAPS2_CUBEMAP_POSITIVEX(0x400),
    DDSCAPS2_CUBEMAP_NEGATIVEX(0x800),
    DDSCAPS2_CUBEMAP_POSITIVEY(0x1000),
    DDSCAPS2_CUBEMAP_NEGATIVEY(0x2000),
    DDSCAPS2_CUBEMAP_POSITIVEZ(0x4000),
    DDSCAPS2_CUBEMAP_NEGATIVEZ(0x8000),
    DDSCAPS2_VOLUME(0x200000),
    ;

    private static final DdsHeaderCaps2[] VALUES = values();
    public static final Set<DdsHeaderCaps2> DDSCAPS2_CUBEMAP_ALL_FACES = EnumSet.of(
        DDSCAPS2_CUBEMAP_POSITIVEX, DDSCAPS2_CUBEMAP_NEGATIVEX,
        DDSCAPS2_CUBEMAP_POSITIVEY, DDSCAPS2_CUBEMAP_NEGATIVEY,
        DDSCAPS2_CUBEMAP_POSITIVEZ, DDSCAPS2_CUBEMAP_NEGATIVEZ);


    private final int value;

    DdsHeaderCaps2(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Set<DdsHeaderCaps2> fromValue(int value) {
        var result = EnumSet.noneOf(DdsHeaderCaps2.class);
        for (var flag : VALUES) {
            if ((value & flag.value) == flag.value) {
                result.add(flag);
                value &= ~flag.value;
            }
        }
        if (value != 0) {
            throw new IllegalArgumentException("Unknown DdsHeaderCaps2 value: " + value);
        }
        return result;
    }
}
