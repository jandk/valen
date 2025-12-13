package be.twofold.valen.export.dds;

import be.twofold.valen.core.util.*;

import java.util.*;

public enum DdsPixelFormatFlags implements FlagEnum {
    DDPF_ALPHAPIXELS(0x1),
    DDPF_ALPHA(0x2),
    DDPF_FOURCC(0x4),
    DDPF_RGB(0x40),
    DDPF_YUV(0x200),
    DDPF_LUMINANCE(0x20000),
    ;

    private final int value;

    DdsPixelFormatFlags(int value) {
        this.value = value;
    }

    public static Set<DdsPixelFormatFlags> fromValue(int value) {
        return FlagEnum.fromValue(DdsPixelFormatFlags.class, value);
    }

    @Override
    public int value() {
        return value;
    }
}
