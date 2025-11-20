package be.twofold.valen.export.dds;

import java.util.*;

public enum DdsHeaderFlags {
    DDSD_CAPS(0x1),
    DDSD_HEIGHT(0x2),
    DDSD_WIDTH(0x4),
    DDSD_PITCH(0x8),
    DDSD_PIXELFORMAT(0x1000),
    DDSD_MIPMAPCOUNT(0x20000),
    DDSD_LINEARSIZE(0x80000),
    DDSD_DEPTH(0x800000),
    ;

    private static final DdsHeaderFlags[] VALUES = values();
    private final int value;

    DdsHeaderFlags(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Set<DdsHeaderFlags> fromValue(int value) {
        var result = EnumSet.noneOf(DdsHeaderFlags.class);
        for (var flag : VALUES) {
            if ((value & flag.value) == flag.value) {
                result.add(flag);
                value &= ~flag.value;
            }
        }
        if (value != 0) {
            throw new IllegalArgumentException("Unknown DdsHeaderFlag value: " + value);
        }
        return result;
    }
}
