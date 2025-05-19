package be.twofold.valen.game.idtech.geometry;

import java.util.*;

public enum GeometryVertexMask {
    POSITION_SHORT(0x20, 8),
    POSITION(0x01, 12),
    NORMAL_TANGENT(0x14, 8),
    LIGHTMAP_UV_SHORT(0x40, 4),
    LIGHTMAP_UV(0x02, 8),
    MATERIAL_UV_SHORT(0x020000, 4),
    MATERIAL_UV(0x8000, 8),
    MATERIAL_UV1(0x08000000, 8),
    MATERIAL_UV2(0x10000000, 8),
    MATERIAL_UV1_SHORT(0x20000000, 4),
    MATERIAL_UV2_SHORT(0x40000000, 4),
    COLOR(0x08, 4),
    // TODO: Check these
    SKINNING_8(0x80, 12),
    SKINNING_1(0x01000000, 0),
    SKINNING_4(0x02000000, 4),
    SKINNING_6(0x04000000, 8),
    ;

    public static final List<GeometryVertexMask> FixedOrder = List.of(
        POSITION_SHORT,
        POSITION,
        LIGHTMAP_UV_SHORT,
        LIGHTMAP_UV,
        NORMAL_TANGENT,
        COLOR,
        MATERIAL_UV_SHORT,
        MATERIAL_UV
    );

    private final int mask;
    private final int size;

    GeometryVertexMask(int mask, int size) {
        this.mask = mask;
        this.size = size;
    }

    public static GeometryVertexMask from(int mask) {
        return Arrays.stream(values())
            .filter(type -> type.mask == mask)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown mask: " + mask));
    }

    public static List<GeometryVertexMask> fromMultiple(int mask) {
        var result = new ArrayList<GeometryVertexMask>();
        for (GeometryVertexMask value : values()) {
            if ((mask & value.mask) == value.mask) {
                result.add(value);
                mask &= ~value.mask;
            }
        }
        if (mask != 0) {
            throw new IllegalArgumentException("Some bits left over: " + mask);
        }
        return result;
    }

    public int mask() {
        return mask;
    }

    public int size() {
        return size;
    }
}
