package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.td.*;

import java.io.*;

public class TDReader implements Reader<TDValue.TDObject> {
    @Override
    public TDValue.TDObject read(Archive archive, Asset asset, DataSource source) throws IOException {
        TDParser parser = new TDParser(new InputStreamReader(new ByteArrayInputStream(source.readBytes((int) source.size()))));
        return parser.parse();
    }

    @Override
    public boolean canRead(AssetID id) {
        if (id instanceof EmperorAssetId emperorAssetId) {
            return emperorAssetId.fileName().endsWith(".td");
        }
        return false;
    }
}
