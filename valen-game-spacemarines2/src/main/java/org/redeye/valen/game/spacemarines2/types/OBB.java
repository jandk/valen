package org.redeye.valen.game.spacemarines2.types;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record OBB(
    Vector3 origin,
    Vector3 xAxis,
    Vector3 yAxis,
    Vector3 zAxis,
    Vector3 size
) {

    public static OBB read(DataSource source) throws IOException {
        return new OBB(source.readVector3(), source.readVector3(), source.readVector3(), source.readVector3(), source.readVector3());
    }
}
