package org.redeye.valen.game.spacemarines2.serializers.template;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.io.*;
import java.util.*;

public class FVFSerializer implements FioSerializer<Set<FVF>> {

    @Override
    public Set<FVF> load(DataSource source) throws IOException {
        var count = source.readShort();
        return FVF.fromCode(source.readBytes((count + 7) / 8));
    }

    @Override
    public int flags() {
        return 0;
    }
}
