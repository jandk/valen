package be.twofold.valen.game.eternal.reader.havokshape;

import java.util.*;

enum HkOption {
    Format(0x01),
    SubType(0x02),
    Version(0x04),
    SizeAlign(0x08),
    Flags(0x10),
    Fields(0x20),
    Interfaces(0x40),
    Attribute(0x80);

    private final int code;

    HkOption(int code) {
        this.code = code;
    }

    public static Set<HkOption> fromCode(int code) {
        Set<HkOption> flags = EnumSet.noneOf(HkOption.class);
        for (HkOption flag : HkOption.values()) {
            if ((code & flag.code) != 0) {
                flags.add(flag);
            }
        }
        return flags;
    }
}
