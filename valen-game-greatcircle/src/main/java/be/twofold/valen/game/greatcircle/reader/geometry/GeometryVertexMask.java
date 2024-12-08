package be.twofold.valen.game.greatcircle.reader.geometry;

import java.util.*;

public enum GeometryVertexMask {
    WGVS_POSITION_SHORT(0, 0x20, 8),
    WGVS_POSITION(1, 0x01, 12),
    WGVS_NORMAL_TANGENT(2, 0x14, 8),
    WGVS_LIGHTMAP_UV_SHORT(3, 0x40, 4),
    WGVS_LIGHTMAP_UV(4, 0x02, 8),
    WGVS_MATERIAL_UV_SHORT(5, 0x00020000, 4),
    WGVS_MATERIAL_UV(6, 0x8000, 8),
    WGVS_COLOR(7, 0x08, 4),
    WGVS_MATERIALS(8, 0x00010000, 8);

    public static final List<GeometryVertexMask> FixedOrder = List.of(
        WGVS_POSITION_SHORT,
        WGVS_POSITION,
        WGVS_LIGHTMAP_UV_SHORT,
        WGVS_LIGHTMAP_UV,
        WGVS_NORMAL_TANGENT,
        WGVS_COLOR,
        WGVS_MATERIAL_UV_SHORT,
        WGVS_MATERIAL_UV,
        WGVS_MATERIALS
    );

    private final int id;
    private final int mask;
    private final int size;

    GeometryVertexMask(int id, int mask, int size) {
        this.id = id;
        this.mask = mask;
        this.size = size;
    }

    public static GeometryVertexMask from(int mask) {
        return Arrays.stream(values())
            .filter(type -> type.mask == mask)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown mask: " + mask));
    }

    public int id() {
        return id;
    }

    public int mask() {
        return mask;
    }

    public int size() {
        return size;
    }
}
