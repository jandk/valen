package org.redeye.valen.game.halflife.mdl.v10;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record StudioTriVert(int vertexIdx, int normalIdx, Vector2 uv) {

    public static StudioTriVert read(DataSource source) throws IOException {
        return new StudioTriVert(Short.toUnsignedInt(source.readShort()), Short.toUnsignedInt(source.readShort()),
            new Vector2(Short.toUnsignedInt(source.readShort()), Short.toUnsignedInt(source.readShort())));
    }
}
