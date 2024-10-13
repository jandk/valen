package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.util.*;

import java.math.*;
import java.util.*;

public enum FVF implements ValueEnum<Long> {
    VERT(0x1),
    VERT_4D(0x2),
    VERT_2D(0x4),
    VERT_COMPR(0x8),
    MASKING_FLAGS(0x10),
    BS_INFO(0x20),
    WEIGHT4(0x40),
    WEIGHT8(0x80),
    INDICES(0x100),
    INDICES16(0x200),
    NORM(0x400),
    NORM_COMPR(0x800),
    NORM_IN_VERT4(0x1000),
    TANG0(0x2000),
    TANG1(0x4000),
    TANG2(0x8000),
    TANG3(0x10000),
    TANG4(0x20000),
    TANG_COMPR(0x40000),
    COLOR0(0x80000),
    COLOR1(0x100000),
    COLOR2(0x200000),
    COLOR3(0x400000),
    COLOR4(0x800000),
    COLOR5(0x1000000),
    TEX0(0x2000000),
    TEX1(0x4000000),
    TEX2(0x8000000),
    TEX3(0x10000000),
    TEX4(0x20000000),
    // TEX5(0x40000000),
    TEX0_COMPR(0x40000000),
    TEX1_COMPR(0x80000000L),
    TEX2_COMPR(0x100000000L),
    TEX3_COMPR(0x200000000L),
    TEX4_COMPR(0x400000000L),
    // TEX5_COMPR(0x800000000L),
    TEX0_4D(0x800000000L),
    TEX1_4D(0x1000000000L),
    TEX2_4D(0x2000000000L),
    TEX3_4D(0x4000000000L),
    TEX4_4D(0x8000000000L),
    // TEX5_4D(0x10000000000L),
    TEX0_4D_BYTE(0x10000000000L),
    TEX1_4D_BYTE(0x20000000000L),
    TEX2_4D_BYTE(0x40000000000L),
    TEX3_4D_BYTE(0x80000000000L),
    TEX4_4D_BYTE(0x100000000000L);
    // TEX5_4D_BYTE(0x1000000000000L);


    private final long value;

    FVF(long value) {
        this.value = value;
    }

    @Override
    public Long value() {
        return value;
    }

    public static Set<FVF> fromCode(long code) {
        Set<FVF> flags = EnumSet.noneOf(FVF.class);
        for (FVF flag : FVF.values()) {
            if ((code & flag.value) != 0) {
                flags.add(flag);
            }
        }
        return flags;
    }

    public static Set<FVF> fromCode(BigInteger code) {
        if (code.bitCount() > 64) {
            throw new IllegalArgumentException("Code is too big");
        }
        Set<FVF> flags = EnumSet.noneOf(FVF.class);
        for (FVF flag : FVF.values()) {
            if (code.and(BigInteger.valueOf(flag.value)).equals(BigInteger.valueOf(flag.value))) {
                flags.add(flag);
            }
        }
        return flags;
    }
}
