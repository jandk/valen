package org.redeye.valen.game.spacemarines2.serializers.terrain;

import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.types.terrain.*;

import java.io.*;

public class TerrainBlockByteArraySerializer implements FioSerializer<TerrainBlockArray<Byte>> {
    @Override
    public TerrainBlockArray<Byte> load(DataSource source) throws IOException {
        return TerrainBlockArray.readByte(source);
    }

    @Override
    public int flags() {
        return 0;
    }
}
