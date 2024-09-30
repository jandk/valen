package org.redeye.valen.game.spacemarines2.serializers.template;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.io.*;
import java.util.*;

public class ObjStateSerializer implements FioSerializer<Set<ObjState>> {

    @Override
    public Set<ObjState> load(DataSource source) throws IOException {
        return ObjState.fromCode(new FioVarIntFlagsSerializer().load(source));
    }

    @Override
    public int flags() {
        return 0;
    }
}
