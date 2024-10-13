package org.redeye.valen.game.spacemarines2.types.lwi;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record LwiElementDataChildSubItem(
    short id,
    Matrix4 mat
) {
    public static LwiElementDataChildSubItem read(DataSource source) throws IOException {
        return new LwiElementDataChildSubItem(source.readShort(), Matrix4.fromArray(source.readFloats(16)));
    }
}
