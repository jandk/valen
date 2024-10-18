package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.util.*;

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

    public static Set<ObjState> fromCode(byte[] code) {
        return fromBitSet(BitSet.valueOf(code));
    }

    private static EnumSet<ObjState> fromBitSet(BitSet bits) {
        EnumSet<ObjState> flags = EnumSet.noneOf(ObjState.class);
        if (bits.get(0)) flags.add(ObjState.OBJ_ST_VERT_WCS);
        if (bits.get(1)) flags.add(ObjState.OBJ_ST_VALIDATE_GROUP);
        if (bits.get(2)) flags.add(ObjState.OBJ_ST_SKIN_REGULAR);
        if (bits.get(3)) flags.add(ObjState.OBJ_ST_SKIN_COMPOUND);
        // bits.get(4)
        // bits.get(5)
        // bits.get(6)
        if (bits.get(7)) flags.add(ObjState.OBJ_ST_NO_FOG);
        // bits.get(8)
        if (bits.get(9)) flags.add(ObjState.OBJ_ST_NO_SPOT);
        if (bits.get(10)) flags.add(ObjState.OBJ_ST_DOUBLE_SIDED);
        // bits.get(11)
        // bits.get(12)

        if (bits.get(13)) flags.add(ObjState.OBJ_ST_START_OFF_MATRMODEL);
        if (bits.get(14)) flags.add(ObjState.OBJ_ST_IDENTITY_MODEL_MATR);
        // bits.get(15)
        // bits.get(16)
        // bits.get(17)
        // bits.get(18)
        if (bits.get(19)) flags.add(ObjState.OBJ_ST_IS_BONE);
        // bits.get(20)
        // bits.get(21)
        // bits.get(22)
        // bits.get(23)
        // bits.get(24)
        if (bits.get(25)) flags.add(ObjState.OBJ_ST_UNUSED);
        if (bits.get(25)) flags.add(ObjState.OBJ_ST_OFF_SCORCH);
        // bits.get(26)
        // bits.get(27)
        // bits.get(28)
        // bits.get(29)
        // bits.get(30)
        if (bits.get(31)) flags.add(ObjState.OBJ_ST_IS_SKIN_COMPOUND_BONE);
        // bits.get(32)
        if (bits.get(33)) flags.add(ObjState.OBJ_ST_DECAL);
        if (bits.get(34)) flags.add(ObjState.OBJ_ST_COLOR_HAS_FRAME_BLEND);
        if (bits.get(35)) flags.add(ObjState.OBJ_ST_SKIN_WEIGHT_BLENDED);
        if (bits.get(36)) flags.add(ObjState.OBJ_ST_MORPHED_SHAPE);
        if (bits.get(37)) flags.add(ObjState.OBJ_ST_VISIBILITY_OCCLUDER);
        if (bits.get(38)) flags.add(ObjState.OBJ_ST_FOG2_PORTAL);
        if (bits.get(39)) flags.add(ObjState.OBJ_ST_SKIN_DUAL_QUATERNION);
        return flags;
    }
}
