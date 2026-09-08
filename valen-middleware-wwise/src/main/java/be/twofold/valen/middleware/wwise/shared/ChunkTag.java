package be.twofold.valen.middleware.wwise.shared;

import wtf.reversed.toolbox.util.*;

public enum ChunkTag implements ValueEnum<Integer> {
    // package
    AKPK('A' | 'K' << 8 | 'P' << 16 | 'K' << 24),

    // bank
    BKHD('B' | 'K' << 8 | 'H' << 16 | 'D' << 24),
    DATA('D' | 'A' << 8 | 'T' << 16 | 'A' << 24),
    DIDX('D' | 'I' << 8 | 'D' << 16 | 'X' << 24),
    ENVS('E' | 'N' << 8 | 'V' << 16 | 'S' << 24),
    HIRC('H' | 'I' << 8 | 'R' << 16 | 'C' << 24),
    PLAT('P' | 'L' << 8 | 'A' << 16 | 'T' << 24),
    STID('S' | 'T' << 8 | 'I' << 16 | 'D' << 24),
    STMG('S' | 'T' << 8 | 'M' << 16 | 'G' << 24),

    // wave/wem
    JUNK('J' | 'U' << 8 | 'N' << 16 | 'K' << 24),
    LIST('L' | 'I' << 8 | 'S' << 16 | 'T' << 24),
    RIFF('R' | 'I' << 8 | 'F' << 16 | 'F' << 24),
    cue_('c' | 'u' << 8 | 'e' << 16 | ' ' << 24),
    data('d' | 'a' << 8 | 't' << 16 | 'a' << 24),
    fmt_('f' | 'm' << 8 | 't' << 16 | ' ' << 24),
    smpl('s' | 'm' << 8 | 'p' << 16 | 'l' << 24),
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
