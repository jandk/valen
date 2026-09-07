package be.twofold.valen.middleware.wwise.shared;

import wtf.reversed.toolbox.util.*;

public enum ChunkTag implements ValueEnum<Integer> {
    AKPK('A' | 'K' << 8 | 'P' << 16 | 'K' << 24),
    BKHD('B' | 'K' << 8 | 'H' << 16 | 'D' << 24),
    DIDX('D' | 'I' << 8 | 'D' << 16 | 'X' << 24),
    DATA('D' | 'A' << 8 | 'T' << 16 | 'A' << 24),
    HIRC('H' | 'I' << 8 | 'R' << 16 | 'C' << 24),
    STID('S' | 'T' << 8 | 'I' << 16 | 'D' << 24),
    ;

    private final int value;

    ChunkTag(int value) {
        this.value = value;
    }

    public static ChunkTag fromValue(int value) {
        return ValueEnum.fromValue(ChunkTag.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
