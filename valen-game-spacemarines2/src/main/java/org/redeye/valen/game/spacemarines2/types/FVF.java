package org.redeye.valen.game.spacemarines2.types;

import be.twofold.valen.core.util.*;

import java.math.*;
import java.util.*;

public enum FVF implements ValueEnum<Long> {
    OBJ_FVF_VERT(0x1),
    OBJ_FVF_VERT_4D(0x2),
    OBJ_FVF_VERT_2D(0x4),
    OBJ_FVF_VERT_COMPR(0x8),
    OBJ_FVF_MASKING_FLAGS(0x10),
    OBJ_FVF_BS_INFO(0x20),
    OBJ_FVF_WEIGHT4(0x40),
    OBJ_FVF_WEIGHT8(0x80),
    OBJ_FVF_INDICES(0x100),
    OBJ_FVF_INDICES16(0x200),
    OBJ_FVF_NORM(0x400),
    OBJ_FVF_NORM_COMPR(0x800),
    OBJ_FVF_NORM_IN_VERT4(0x1000),
    OBJ_FVF_TANG0(0x2000),
    OBJ_FVF_TANG1(0x4000),
    OBJ_FVF_TANG2(0x8000),
    OBJ_FVF_TANG3(0x10000),
    OBJ_FVF_TANG4(0x20000),
    OBJ_FVF_TANG_COMPR(0x40000),
    OBJ_FVF_COLOR0(0x80000),
    OBJ_FVF_COLOR1(0x100000),
    OBJ_FVF_COLOR2(0x200000),
    OBJ_FVF_COLOR3(0x400000),
    OBJ_FVF_COLOR4(0x800000),
    OBJ_FVF_COLOR5(0x1000000),
    OBJ_FVF_TEX0(0x2000000),
    OBJ_FVF_TEX1(0x4000000),
    OBJ_FVF_TEX2(0x8000000),
    OBJ_FVF_TEX3(0x10000000),
    OBJ_FVF_TEX4(0x20000000),
    OBJ_FVF_TEX5(0x40000000),
    OBJ_FVF_TEX0_COMPR(0x80000000L),
    OBJ_FVF_TEX1_COMPR(0x100000000L),
    OBJ_FVF_TEX2_COMPR(0x200000000L),
    OBJ_FVF_TEX3_COMPR(0x400000000L),
    OBJ_FVF_TEX4_COMPR(0x800000000L),
    OBJ_FVF_TEX5_COMPR(0x1000000000L),
    OBJ_FVF_TEX0_4D(0x2000000000L),
    OBJ_FVF_TEX1_4D(0x4000000000L),
    OBJ_FVF_TEX2_4D(0x8000000000L),
    OBJ_FVF_TEX3_4D(0x10000000000L),
    OBJ_FVF_TEX4_4D(0x20000000000L),
    OBJ_FVF_TEX5_4D(0x40000000000L),
    OBJ_FVF_TEX0_4D_BYTE(0x80000000000L),
    OBJ_FVF_TEX1_4D_BYTE(0x100000000000L),
    OBJ_FVF_TEX2_4D_BYTE(0x200000000000L),
    OBJ_FVF_TEX3_4D_BYTE(0x400000000000L),
    OBJ_FVF_TEX4_4D_BYTE(0x800000000000L),
    OBJ_FVF_TEX5_4D_BYTE(0x1000000000000L);


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
        Set<FVF> flags = EnumSet.noneOf(FVF.class);
        for (FVF flag : FVF.values()) {
            if (code.and(BigInteger.valueOf(flag.value)).equals(BigInteger.valueOf(flag.value))) {
                flags.add(flag);
            }
        }
        return flags;
    }
}
