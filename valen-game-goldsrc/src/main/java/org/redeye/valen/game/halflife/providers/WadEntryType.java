package org.redeye.valen.game.halflife.providers;

import be.twofold.valen.core.util.*;

import java.util.*;

public enum WadEntryType implements ValueEnum<Integer> {
    PALETTE(64),
    COLORMAP(65),
    QPIC(66),
    MIPTEX(67),
    RAW(68),
    COLORMAP2(69),
    FONT(70);

    private static final Map<Integer, WadEntryType> MAP = ValueEnum.valueMap(WadEntryType.class);


    private final int code;

    WadEntryType(int code) {
        this.code = code;
    }

    public static WadEntryType forValue(int value) {
        return ValueEnum.fromValue(MAP, value)
            .orElseThrow(() -> new IllegalArgumentException("Invalid WadEntryType: " + value));
    }

    @Override
    public Integer value() {
        return code;
    }
}
