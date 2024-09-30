package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.util.*;

public enum GeomStreamSlot implements ValueEnum<Integer> {
    OBJ_GEOM_STRM_VERT(0x0),
    OBJ_GEOM_STRM_WEIGHT(0x1),
    OBJ_GEOM_STRM_BONE_INDEX(0x2),
    OBJ_GEOM_STRM_INTERLEAVED(0x3),
    OBJ_GEOM_STRM_INSTANCED(0x4),
    OBJ_GEOM_STRM_VERT_MAX(0x5),
    OBJ_GEOM_STRM_FACE(0x5),
    OBJ_GEOM_STRM_TOTAL(0x6);


    private final int value;

    GeomStreamSlot(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }
}
