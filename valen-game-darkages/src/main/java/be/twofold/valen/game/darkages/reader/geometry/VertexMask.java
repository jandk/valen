package be.twofold.valen.game.darkages.reader.geometry;

import wtf.reversed.toolbox.util.*;

import java.util.*;

public enum VertexMask {
    VERTEX_MASK_POSITION(0x01),
    VERTEX_MASK_POSITION_SHORT(0x20),
    VERTEX_MASK_LIGHTMAP_UV(0x02),
    VERTEX_MASK_LIGHTMAP_UV_SHORT(0x40),
    VERTEX_MASK_ST1(0x0400),
    VERTEX_MASK_ST1_SHORT(0x0800),
    VERTEX_MASK_NORMAL(0x04),
    VERTEX_MASK_COLOR(0x08),
    VERTEX_MASK_TANGENT(0x10),
    VERTEX_MASK_MATERIAL_UV(0x8000),
    VERTEX_MASK_MATERIAL_UV1(0x08000000),
    VERTEX_MASK_MATERIAL_UV2(0x10000000),
    VERTEX_MASK_MATERIAL_UV_SHORT(0x020000),
    VERTEX_MASK_MATERIAL_UV1_SHORT(0x20000000),
    VERTEX_MASK_MATERIAL_UV2_SHORT(0x40000000),
    VERTEX_MASK_SKINNING(0x80),
    VERTEX_MASK_SKINNING_1(0x01000000),
    VERTEX_MASK_SKINNING_4(0x02000000),
    VERTEX_MASK_SKINNING_6(0x04000000),
    INDEX_MASK_INDEX_LONG(0x0100),
    INDEX_MASK_INDEX_STRIP(0x0200),
    VERTEX_MASK_SLUG_NORMAL_DIR(0x2000),
    VERTEX_MASK_SLUG_VERTEX_COORDS(0x4000),
    VERTEX_MASK_SLUG_GLYPH_DATA(0x040000),
    VERTEX_MASK_SLUG_JACOBIAN(0x080000),
    VERTEX_MASK_SLUG_BANDING(0x100000),
    VERTEX_MASK_SLUG_POSITION_2D(0x200000),
    VERTEX_MASK_UI_QUAD_SIZE(0x4000),
    VERTEX_MASK_UI_MASK_UV(0x040000),
    VERTEX_MASK_UI_SHAPE_INFO(0x080000),
    VERTEX_MASK_UI_BORDER_INFO(0x10),
    VERTEX_MASK_UI_TEXTURE_IDS(0x400000),
    VERTEX_MASK_UI_CLIP_MASK_FLAGS(0x800000),
    ;

    private final int mask;

    VertexMask(int mask) {
        this.mask = mask;
    }

    public static List<VertexMask> getMasks(int value) {
        var result = new ArrayList<VertexMask>();
        for (VertexMask mask : values()) {
            if ((value & mask.mask) == mask.mask) {
                result.add(mask);
                value &= ~mask.mask;
            }
        }
        Check.state(value == 0, "There's left over bits");
        return result;
    }
}
