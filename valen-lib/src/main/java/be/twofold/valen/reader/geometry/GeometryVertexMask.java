package be.twofold.valen.reader.geometry;

import java.util.*;

public enum GeometryVertexMask {
    WGVS_POSITION_SHORT(0, 0x00020),
    WGVS_POSITION(1, 0x00001),
    WGVS_NORMAL_TANGENT(2, 0x00014),
    WGVS_LIGHTMAP_UV_SHORT(3, 0x00040),
    WGVS_LIGHTMAP_UV(4, 0x00002),
    WGVS_MATERIAL_UV_SHORT(5, 0x20000),
    WGVS_MATERIAL_UV(6, 0x08000),
    WGVS_COLOR(7, 0x00008),
    WGVS_MATERIALS(8, 0x10000);

    private final int id;
    private final int mask;

    GeometryVertexMask(int id, int mask) {
        this.id = id;
        this.mask = mask;
    }

    public int getId() {
        return id;
    }

    public int getMask() {
        return mask;
    }

    public static GeometryVertexMask from(int mask) {
        return Arrays.stream(values())
            .filter(type -> type.mask == mask)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown mask: " + mask));
    }
}
