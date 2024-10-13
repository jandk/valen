package org.redeye.valen.game.spacemarines2.types.lwi;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record LwiElementData(
    int elemId,
    int hashedMaterialOverride,
    List<LwiElementDataChild> children
) {

    public static LwiElementData read(DataSource source, int version) throws IOException {
        int elemId = source.readInt();
        int hashedMaterialOverride = 0;
        if (version >= 7) {
            hashedMaterialOverride = source.readInt();
        }
        List<LwiElementDataChild> children = new ArrayList<>();
        var count = source.readInt();
        for (int i = 0; i < count; i++) {
            children.add(LwiElementDataChild.read(source, version));
        }
        return new LwiElementData(elemId, hashedMaterialOverride, children);
    }
}
