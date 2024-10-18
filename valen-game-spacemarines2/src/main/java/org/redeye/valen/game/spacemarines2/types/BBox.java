package org.redeye.valen.game.spacemarines2.types;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record BBox(Vector3 min, Vector3 max) {

    public static BBox read(DataSource source) throws IOException {
        return new BBox(Vector3.read(source), Vector3.read(source));
    }

}
