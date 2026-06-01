package be.twofold.valen.game.doom.readers.model;

import wtf.reversed.toolbox.util.*;

import java.util.*;

public enum VertexMask implements FlagEnum {
    VERTEX_MASK_XYZ(1),
    VERTEX_MASK_XYZ1(4096),
    VERTEX_MASK_ST(2),
    VERTEX_MASK_ST1(1024),
    VERTEX_MASK_NORMAL(4),
    VERTEX_MASK_COLOR(8),
    VERTEX_MASK_TANGENT(16),
    VERTEX_MASK_PACKED_INPUTS(131072),
    VERTEX_MASK_MORPH(128),
    VERTEX_MASK_MORPH_NORMAL(16384),
    VERTEX_MASK_XYZ_SHORT(32),
    VERTEX_MASK_ST_SHORT(64),
    VERTEX_MASK_ST1_SHORT(2048),
    VERTEX_MASK_INDEX_LONG(256),
    VERTEX_MASK_INDEX_STRIP(512),
    VERTEX_MASK_VMTR_TC(32768),
    VERTEX_MASK_VMTR_SB(65536),
    ;

    private final int value;

    VertexMask(int value) {
        this.value = value;
    }

    public static Set<VertexMask> fromValue(int value) {
        return FlagEnum.fromValue(VertexMask.class, value);
    }

    @Override
    public int value() {
        return value;
    }
}
