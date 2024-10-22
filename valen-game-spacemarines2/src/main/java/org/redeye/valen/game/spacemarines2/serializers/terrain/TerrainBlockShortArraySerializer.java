package org.redeye.valen.game.spacemarines2.serializers.terrain;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.terrain.*;

import java.io.*;

public class TerrainBlockShortArraySerializer implements FioSerializer<TerrainBlockArray<Short>> {
    @Override
    public TerrainBlockArray<Short> load(DataSource source) throws IOException {
        return TerrainBlockArray.readShort(source);
    }

    @Override
    public int flags() {
        return 0;
    }
}
