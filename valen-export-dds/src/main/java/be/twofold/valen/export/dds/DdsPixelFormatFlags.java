package be.twofold.valen.export.dds;

import java.util.*;

public enum DdsPixelFormatFlags {
    DDPF_ALPHAPIXELS(0x1),
    DDPF_ALPHA(0x2),
    DDPF_FOURCC(0x4),
    DDPF_RGB(0x40),
    DDPF_YUV(0x200),
    DDPF_LUMINANCE(0x20000),
    ;

    private static final DdsPixelFormatFlags[] VALUES = values();
    private final int value;

    DdsPixelFormatFlags(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Set<DdsPixelFormatFlags> fromValue(int value) {
        var result = EnumSet.noneOf(DdsPixelFormatFlags.class);
        for (var flag : VALUES) {
            if ((value & flag.value) == flag.value) {
                result.add(flag);
                value &= ~flag.value;
            }
        }
        if (value != 0) {
            throw new IllegalArgumentException("Unknown DdsPixelFormatFlags value: " + value);
        }
        return result;
    }
}
