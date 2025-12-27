package be.twofold.valen.export.dds;

import wtf.reversed.toolbox.util.*;

import java.util.*;

public enum DdsHeaderFlags implements FlagEnum {
    DDSD_CAPS(0x1),
    DDSD_HEIGHT(0x2),
    DDSD_WIDTH(0x4),
    DDSD_PITCH(0x8),
    DDSD_PIXELFORMAT(0x1000),
    DDSD_MIPMAPCOUNT(0x20000),
    DDSD_LINEARSIZE(0x80000),
    DDSD_DEPTH(0x800000),
    ;

    private final int value;

    DdsHeaderFlags(int value) {
        this.value = value;
    }

    public static Set<DdsHeaderFlags> fromValue(int value) {
        return FlagEnum.fromValue(DdsHeaderFlags.class, value);
    }

    @Override
    public int value() {
        return value;
    }
}
