package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.util.*;

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
    TEX5(0x40000000),
    TEX0_COMPR(0x80000000L),
    TEX1_COMPR(0x100000000L),
    TEX2_COMPR(0x200000000L),
    TEX3_COMPR(0x400000000L),
    TEX4_COMPR(0x800000000L),
    TEX5_COMPR(0x1000000000L),
    TEX0_4D(0x2000000000L),
    TEX1_4D(0x4000000000L),
    TEX2_4D(0x8000000000L),
    TEX3_4D(0x10000000000L),
    TEX4_4D(0x20000000000L),
    TEX5_4D(0x40000000000L),
    TEX0_4D_BYTE(0x80000000000L),
    TEX1_4D_BYTE(0x100000000000L),
    TEX2_4D_BYTE(0x200000000000L),
    TEX3_4D_BYTE(0x400000000000L),
    TEX4_4D_BYTE(0x800000000000L),
    TEX5_4D_BYTE(0x1000000000000L);


    private final long value;

    FVF(long value) {
        this.value = value;
    }

    @Override
    public Long value() {
        return value;
    }

    public static Set<FVF> fromCode(byte[] code) {
        return fromBitSet(BitSet.valueOf(code));
    }

    private static EnumSet<FVF> fromBitSet(BitSet bits) {
        EnumSet<FVF> flags = EnumSet.noneOf(FVF.class);
        if (bits.get(0)) flags.add(FVF.VERT);
        if (bits.get(1)) flags.add(FVF.VERT_4D);
        if (bits.get(2)) flags.add(FVF.VERT_2D);
        if (bits.get(3)) flags.add(FVF.VERT_COMPR);

        if (bits.get(7)) flags.add(FVF.WEIGHT4);

        if (bits.get(9)) flags.add(FVF.INDICES);
        if (bits.get(10)) flags.add(FVF.NORM);
        if (bits.get(11)) flags.add(FVF.NORM_COMPR);

        if (bits.get(12)) flags.add(FVF.TANG0);
        if (bits.get(13)) flags.add(FVF.TANG1);
        if (bits.get(14)) flags.add(FVF.TANG2);
        if (bits.get(15)) flags.add(FVF.TANG3);
        if (bits.get(16)) flags.add(FVF.TANG4);
        if (bits.get(17)) flags.add(FVF.TANG_COMPR);

        if (bits.get(22)) flags.add(FVF.COLOR0);
        if (bits.get(23)) flags.add(FVF.COLOR1);
        if (bits.get(24)) flags.add(FVF.COLOR2);
        if (bits.get(25)) flags.add(FVF.TEX0);
        if (bits.get(26)) flags.add(FVF.TEX1);
        if (bits.get(27)) flags.add(FVF.TEX2);
        if (bits.get(28)) flags.add(FVF.TEX3);
        if (bits.get(29)) flags.add(FVF.TEX4);
        if (bits.get(30)) flags.add(FVF.TEX0_COMPR);
        if (bits.get(31)) flags.add(FVF.TEX1_COMPR);
        if (bits.get(32)) flags.add(FVF.TEX2_COMPR);
        if (bits.get(33)) flags.add(FVF.TEX3_COMPR);
        if (bits.get(34)) flags.add(FVF.TEX4_COMPR);
        if (bits.get(35)) flags.add(FVF.TEX0_4D);
        if (bits.get(36)) flags.add(FVF.TEX1_4D);
        if (bits.get(37)) flags.add(FVF.TEX2_4D);
        if (bits.get(38)) flags.add(FVF.TEX3_4D);
        if (bits.get(39)) flags.add(FVF.TEX4_4D);
        if (bits.get(40)) flags.add(FVF.TEX0_4D_BYTE);
        if (bits.get(41)) flags.add(FVF.TEX1_4D_BYTE);
        if (bits.get(42)) flags.add(FVF.TEX2_4D_BYTE);
        if (bits.get(43)) flags.add(FVF.TEX3_4D_BYTE);
        if (bits.get(44)) flags.add(FVF.TEX4_4D_BYTE);
        if (bits.get(45)) flags.add(FVF.NORM_IN_VERT4);
        if (bits.get(46)) flags.add(FVF.COLOR3);
        if (bits.get(47)) flags.add(FVF.COLOR4);

        if (bits.get(59)) flags.add(FVF.TEX5);
        if (bits.get(60)) flags.add(FVF.TEX5_COMPR);
        if (bits.get(61)) flags.add(FVF.TEX5_4D);
        if (bits.get(62)) flags.add(FVF.TEX5_4D_BYTE);
        if (bits.get(63)) flags.add(FVF.COLOR5);

        if (bits.get(67)) flags.add(FVF.MASKING_FLAGS);
        if (bits.get(68)) flags.add(FVF.BS_INFO);
        if (bits.get(69)) flags.add(FVF.WEIGHT8);
        if (bits.get(70)) flags.add(FVF.INDICES16);

        return flags;
    }
}
