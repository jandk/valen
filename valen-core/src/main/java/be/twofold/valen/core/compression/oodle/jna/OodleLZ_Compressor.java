package be.twofold.valen.core.compression.oodle.jna;

import be.twofold.valen.core.util.*;

public enum OodleLZ_Compressor implements NativeEnum {
    Invalid(-1),
    None(3),
    Kraken(8),
    Leviathan(13),
    Mermaid(9),
    Selkie(11),
    Hydra(12),
    BitKnit(10),
    LZB16(4),
    LZNA(7),
    LZH(0),
    LZHLW(1),
    LZNIB(2),
    LZBLW(5),
    LZA(6),
    Count(14),
    Force32(0x40000000);

    private final int nativeValue;

    OodleLZ_Compressor(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    @Override
    public int nativeValue() {
        return nativeValue;
    }
}
