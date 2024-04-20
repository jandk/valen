package be.twofold.valen.reader.staticmodel;

import java.util.*;

public enum StaticModelFlags {
    Vertices(1 << 0, 12),
    Unknown2(1 << 1, 0),
    PackedNormals(1 << 4 | 1 << 2, 8),
    Colors(1 << 3, 0),
    PackedVertices(1 << 5, 8),
    LightMapUVs(1 << 6, 4),
    PackedUVs(1 << 15, 4),
    Unknown10000(1 << 16, 0),
    UVs(1 << 17, 8);

    private final int mask;
    private final int size;

    StaticModelFlags(int mask, int size) {
        this.mask = mask;
        this.size = size;
    }

    public static EnumSet<StaticModelFlags> fromMask(int mask) {
        var flags = EnumSet.noneOf(StaticModelFlags.class);
        for (var flag : values()) {
            if ((mask & flag.mask) == flag.mask) {
                flags.add(flag);
                mask &= ~flag.mask;
            }
        }
        if (mask != 0) {
            throw new IllegalArgumentException("Unknown flags: " + mask);
        }
        return flags;
    }

    public static int totalSize(Set<StaticModelFlags> modelFlags) {
        return modelFlags.stream()
            .mapToInt(flag -> flag.size)
            .sum();
    }
}
