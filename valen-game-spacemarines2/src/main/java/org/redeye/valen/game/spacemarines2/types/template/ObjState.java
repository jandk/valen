package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.util.*;

import java.math.*;
import java.util.*;

public enum ObjState implements ValueEnum<Integer> {
    OBJ_ST_VERT_WCS(0x1),
    OBJ_ST_VALIDATE_GROUP(0x2),
    OBJ_ST_SKIN_REGULAR(0x4),
    OBJ_ST_SKIN_COMPOUND(0x8),
    OBJ_ST_SKIN_WEIGHT_BLENDED(0x10),
    OBJ_ST_NO_FOG(0x20),
    OBJ_ST_NO_SPOT(0x40),
    OBJ_ST_DOUBLE_SIDED(0x80),
    OBJ_ST_START_OFF_MATRMODEL(0x100),
    OBJ_ST_IDENTITY_MODEL_MATR(0x200),
    OBJ_ST_BELONG_TO_STAT_SCENE(0x400),
    OBJ_ST_IS_BONE(0x800),
    OBJ_ST_IS_SKIN_COMPOUND_BONE(0x1000),
    OBJ_ST_UNUSED(0x4000),
    OBJ_ST_DECAL(0x8000),
    OBJ_ST_COLOR_HAS_FRAME_BLEND(0x10000),
    OBJ_ST_ANIM_FACIAL(0x20000),
    OBJ_ST_ANIM_ROTATION_ONLY(0x40000),
    OBJ_ST_MORPHED_SHAPE(0x80000),
    OBJ_ST_VISIBILITY_OCCLUDER(0x100000),
    OBJ_ST_FOG2_PORTAL(0x200000),
    OBJ_ST_SKIN_DUAL_QUATERNION(0x400000),
    OBJ_ST_VISIBILITY_QUALIFY(0x800000),
    OBJ_ST_FP_MODEL(0x1000000),
    OBJ_ST_DISSABLE_TRANSP_OCCLUSION(0x2000000),
    OBJ_ST_OFF_SCORCH(0x4000000),
    OBJ_ST_LAST_FLAG(0x4000000);

    private final int value;

    ObjState(int value) {
        this.value = value;
    }

    @Override
    public Integer value() {
        return value;
    }

    public static Set<ObjState> fromCode(long code) {
        Set<ObjState> flags = EnumSet.noneOf(ObjState.class);
        for (ObjState flag : ObjState.values()) {
            if ((code & flag.value) != 0) {
                flags.add(flag);
            }
        }
        return flags;
    }

    public static Set<ObjState> fromCode(BigInteger code) {
        Set<ObjState> flags = EnumSet.noneOf(ObjState.class);
        for (ObjState flag : ObjState.values()) {
            if (code.and(BigInteger.valueOf(flag.value)).equals(BigInteger.valueOf(flag.value))) {
                flags.add(flag);
            }
        }
        return flags;
    }
}
