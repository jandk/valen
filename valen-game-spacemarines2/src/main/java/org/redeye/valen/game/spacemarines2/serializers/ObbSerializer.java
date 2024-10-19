package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.io.*;

public class ObbSerializer implements FioSerializer<OBB> {

    @Override
    public OBB load(DataSource source) throws IOException {
        return OBB.read(source);
    }

    @Override
    public int flags() {
        return 16;
    }
}
