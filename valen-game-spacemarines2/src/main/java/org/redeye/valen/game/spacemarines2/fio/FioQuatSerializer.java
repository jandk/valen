package org.redeye.valen.game.spacemarines2.fio;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public class FioQuatSerializer implements FioSerializer<Quaternion> {
    @Override
    public Quaternion load(DataSource source) throws IOException {
        return source.readQuaternion();
    }

    @Override
    public int flags() {
        return 0;
    }
}
