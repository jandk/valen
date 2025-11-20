package be.twofold.valen.export.dds;

import java.util.*;

public enum DdsPixelFormatFourCC {
    DXT1('D' | 'X' << 8 | 'T' << 16 | '1' << 24),
    DXT2('D' | 'X' << 8 | 'T' << 16 | '2' << 24),
    DXT3('D' | 'X' << 8 | 'T' << 16 | '3' << 24),
    DXT4('D' | 'X' << 8 | 'T' << 16 | '4' << 24),
    DXT5('D' | 'X' << 8 | 'T' << 16 | '5' << 24),
    BC4U('B' | 'C' << 8 | '4' << 16 | 'U' << 24),
    BC4S('B' | 'C' << 8 | '4' << 16 | 'S' << 24),
    BC5U('B' | 'C' << 8 | '5' << 16 | 'U' << 24),
    BC5S('B' | 'C' << 8 | '5' << 16 | 'S' << 24),
    RGBG('R' | 'G' << 8 | 'B' << 16 | 'G' << 24),
    GRGB('G' | 'R' << 8 | 'G' << 16 | 'B' << 24),
    YUY2('Y' | 'U' << 8 | 'Y' << 16 | '2' << 24),
    UYVY('U' | 'Y' << 8 | 'V' << 16 | 'Y' << 24),

    // DX10
    DX10('D' | 'X' << 8 | '1' << 16 | '0' << 24),
    ;

    private final int value;

    DdsPixelFormatFourCC(int value) {
        this.value = value;
    }

    public static DdsPixelFormatFourCC fromValue(int value) {
        for (DdsPixelFormatFourCC fourCC : values()) {
            if (fourCC.value == value) {
                return fourCC;
            }
        }
        throw new IllegalArgumentException("Unknown FourCC value: " + HexFormat.of().toHexDigits(value));
    }

    public int getValue() {
        return value;
    }
}
