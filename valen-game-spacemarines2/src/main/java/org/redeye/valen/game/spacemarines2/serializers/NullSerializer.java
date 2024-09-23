package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;

import java.io.*;

public class NullSerializer implements FioSerializer<Void> {

    @Override
    public Void load(DataSource source) throws IOException {
        return null;
    }

    @Override
    public int flags() {
        return 0;
    }

}
