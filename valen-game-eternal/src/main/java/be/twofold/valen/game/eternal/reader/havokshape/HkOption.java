package be.twofold.valen.game.eternal.reader.havokshape;

import be.twofold.valen.core.util.*;

import java.util.*;

enum HkOption implements FlagEnum {
    Format(0x01),
    SubType(0x02),
    Version(0x04),
    SizeAlign(0x08),
    Flags(0x10),
    Fields(0x20),
    Interfaces(0x40),
    Attribute(0x80);

    private final int value;

    HkOption(int value) {
        this.value = value;
    }

    public static Set<HkOption> fromValue(int value) {
        return FlagEnum.fromValue(HkOption.class, value);
    }

    @Override
    public int value() {
        return value;
    }
}
