package be.twofold.valen.reader.model;

import java.util.*;

public enum ModelFlags {
    Vertices(1, 12),
    PackedVertices(32, 8),
    PackedNormals(20, 8),
    LightMapUVs(64, 4),
    UVs(131072, 8),
    PackedUVs(32768, 4),
    Colors(8, 0),
    Unknown2(2, 0),
    Unknown10000(65536, 0);

    private final int mask;
    private final int size;

    ModelFlags(int mask, int size) {
        this.mask = mask;
        this.size = size;
    }

    public static EnumSet<ModelFlags> fromMask(int mask) {
        EnumSet<ModelFlags> flags = EnumSet.noneOf(ModelFlags.class);
        for (ModelFlags flag : values()) {
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

    public static int totalSize(Set<ModelFlags> modelFlags) {
        return modelFlags.stream()
            .mapToInt(flag -> flag.size)
            .sum();
    }
}
