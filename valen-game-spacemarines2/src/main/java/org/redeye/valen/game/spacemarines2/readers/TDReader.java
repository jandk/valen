package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import com.google.gson.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.psSection.*;

import java.io.*;

public class TDReader implements Reader<JsonObject> {
    @Override
    public JsonObject read(Archive archive, Asset asset, DataSource source) throws IOException {
        return PsSectionAscii.parseFromDataSource(source);
    }

    @Override
    public boolean canRead(AssetID id) {
        if (id instanceof EmperorAssetId emperorAssetId) {
            return emperorAssetId.fileName().endsWith(".td");
        }
        return false;
    }
}
