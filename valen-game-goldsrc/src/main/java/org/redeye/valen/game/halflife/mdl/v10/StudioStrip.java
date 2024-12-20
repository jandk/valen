package org.redeye.valen.game.halflife.mdl.v10;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StudioStrip(List<StudioTriVert> tris, boolean isFan) {

    public static StudioStrip read(DataSource source) throws IOException {
        int triCount = source.readShort();
        if (triCount == 0) {
            return null;
        }
        var isFan = triCount < 0;
        triCount = Math.abs(triCount);

        var tris = new ArrayList<StudioTriVert>();
        for (int i = 0; i < triCount; i++) {
            tris.add(StudioTriVert.read(source));
        }
        return new StudioStrip(tris, isFan);
    }
}
