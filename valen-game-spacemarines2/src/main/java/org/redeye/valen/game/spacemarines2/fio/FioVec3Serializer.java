package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public class FioVec3Serializer implements FioSerializer<Vector3> {
    @Override
    public Vector3 load(DataSource source) throws IOException {
        return source.readVector3();
    }

    @Override
    public int flags() {
        return 0;
    }
}
