package be.twofold.valen.game.source.readers.vtf;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

public enum VtfResourceTag implements ValueEnum<Bytes> {
    LOW_RES(Bytes.wrap(new byte[]{0x01, 0, 0})),
    PARTICLE(Bytes.wrap(new byte[]{0x10, 0, 0})),
    HIGH_RES(Bytes.wrap(new byte[]{0x30, 0, 0})),
    CRC(Bytes.wrap(new byte[]{'C', 'R', 'C'})),
    LOD(Bytes.wrap(new byte[]{'L', 'O', 'D'}));

    private static final VtfResourceTag[] VALUES = values();
    private final Bytes value;

    VtfResourceTag(Bytes value) {
        this.value = value;
    }

    public static VtfResourceTag fromValue(Bytes value) {
        return ValueEnum.fromValue(VtfResourceTag.class, value);
    }

    @Override
    public Bytes value() {
        return value;
    }
}
