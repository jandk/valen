package org.redeye.valen.game.spacemarines2.serializers;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.io.*;

public class BoxSerializer implements FioSerializer<Box> {

    @Override
    public Box load(DataSource source) throws IOException {
        return Box.read(source);
    }

    @Override
    public int flags() {
        return 0;
    }
}
