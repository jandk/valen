package be.twofold.valen.game.goldsrc.reader.mdl.v10;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StudioStrip(
    List<StudioTriVert> tris,
    boolean isFan
) {
    public static StudioStrip read(BinarySource source) throws IOException {
        int triCount = source.readShort();
        if (triCount == 0) {
            return null;
        }
        var isFan = triCount < 0;
        triCount = Math.abs(triCount);
        var tris = source.readObjects(triCount, StudioTriVert::read);
        return new StudioStrip(tris, isFan);
    }
}
