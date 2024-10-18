package org.redeye.valen.game.spacemarines2.types;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Box(
    Vector3 pos,
    Vector3 extents
) {

    public static Box read(DataSource source) throws IOException {
        return new Box(Vector3.read(source), Vector3.read(source));
    }
}
