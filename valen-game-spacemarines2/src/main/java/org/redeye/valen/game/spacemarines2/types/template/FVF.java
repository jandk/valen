package org.redeye.valen.game.spacemarines2.types.template;

import java.util.*;

public enum FVF {
    VERT(0, 0x1),
    VERT_4D(1, 0x2),
    VERT_2D(2, 0x4),
    VERT_COMPR(3, 0x8),
    MASKING_FLAGS(67, 0x10),
    BS_INFO(68, 0x20),
    WEIGHT4(7, 0x40),
    WEIGHT8(69, 0x80),
    INDICES(9, 0x100),
    INDICES16(70, 0x200),
    NORM(10, 0x400),
    NORM_COMPR(11, 0x800),
    NORM_IN_VERT4(45, 0x1000),
    TANG0(12, 0x2000),
    TANG1(13, 0x4000),
    TANG2(14, 0x8000),
    TANG3(15, 0x10000),
    TANG4(16, 0x20000),
    TANG_COMPR(17, 0x40000),
    COLOR0(22, 0x80000),
    COLOR1(23, 0x100000),
    COLOR2(24, 0x200000),
    COLOR3(46, 0x400000),
    COLOR4(47, 0x800000),
    COLOR5(63, 0x1000000),
    TEX0(25, 0x2000000),
    TEX1(26, 0x4000000),
    TEX2(27, 0x8000000),
    TEX3(28, 0x10000000),
    TEX4(29, 0x20000000),
    TEX5(59, 0x40000000),
    TEX0_COMPR(30, 0x80000000L),
    TEX1_COMPR(31, 0x100000000L),
    TEX2_COMPR(32, 0x200000000L),
    TEX3_COMPR(33, 0x400000000L),
    TEX4_COMPR(34, 0x800000000L),
    TEX5_COMPR(60, 0x1000000000L),
    TEX0_4D(35, 0x2000000000L),
    TEX1_4D(36, 0x4000000000L),
    TEX2_4D(37, 0x8000000000L),
    TEX3_4D(38, 0x10000000000L),
    TEX4_4D(39, 0x20000000000L),
    TEX5_4D(61, 0x40000000000L),
    TEX0_4D_BYTE(40, 0x80000000000L),
    TEX1_4D_BYTE(41, 0x100000000000L),
    TEX2_4D_BYTE(42, 0x200000000000L),
    TEX3_4D_BYTE(43, 0x400000000000L),
    TEX4_4D_BYTE(44, 0x800000000000L),
    TEX5_4D_BYTE(62, 0x1000000000000L);

    private final int bit;
    private final long originalFlag;

    FVF(int bit, long originalFlag) {
        this.bit = bit;
        this.originalFlag = originalFlag;
    }

    public static Set<FVF> fromCode(byte[] code) {
        var bits = BitSet.valueOf(code);
        var flags = EnumSet.noneOf(FVF.class);
        for (var value : FVF.values()) {
            if (bits.get(value.bit)) {
                flags.add(value);
            }
        }
        return flags;
    }
}
