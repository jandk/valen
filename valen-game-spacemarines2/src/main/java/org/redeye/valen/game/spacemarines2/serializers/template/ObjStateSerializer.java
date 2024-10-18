package org.redeye.valen.game.spacemarines2.serializers.template;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.io.*;
import java.util.*;

public class ObjStateSerializer implements FioSerializer<Set<ObjState>> {

    @Override
    public Set<ObjState> load(DataSource source) throws IOException {
        var count = source.readShort();
        return ObjState.fromCode(source.readBytes((count + 7) / 8));
    }

    @Override
    public int flags() {
        return 0;
    }
}
