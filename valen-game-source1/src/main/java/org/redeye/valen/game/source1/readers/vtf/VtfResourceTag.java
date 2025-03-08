package org.redeye.valen.game.source1.readers.vtf;

import java.util.*;

public enum VtfResourceTag {
    LOW_RES(new byte[]{0x01, 0, 0}),
    HIGH_RES(new byte[]{0x30, 0, 0}),
    CRC(new byte[]{'C', 'R', 'C'}),
    ;

    private static final VtfResourceTag[] VALUES = values();
    private final byte[] value;

    VtfResourceTag(byte[] value) {
        this.value = value;
    }

    public static VtfResourceTag fromValue(byte[] value) {
        for (var tag : VALUES) {
            if (Arrays.equals(value, tag.value)) {
                return tag;
            }
        }
        throw new UnsupportedOperationException("Unknown tag: " + Arrays.toString(value));
    }
}
