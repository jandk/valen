package be.twofold.valen.game.goldsrc.reader.wad;

import be.twofold.valen.core.util.*;

public enum WadEntryType implements ValueEnum<Integer> {
    PALETTE(64),
    COLORMAP(65),
    QPIC(66),
    MIPTEX(67),
    RAW(68),
    COLORMAP2(69),
    FONT(70);

    private final int value;

    WadEntryType(int value) {
        this.value = value;
    }

    public static WadEntryType fromValue(int value) {
        return ValueEnum.fromValue(WadEntryType.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
