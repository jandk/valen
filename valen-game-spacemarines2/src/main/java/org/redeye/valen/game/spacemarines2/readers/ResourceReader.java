package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import com.google.gson.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.archives.*;
import org.yaml.snakeyaml.*;

import java.io.*;
import java.util.*;

public class ResourceReader implements Reader<JsonObject> {
    @Override
    public JsonObject read(EmperorArchive archive, Asset asset, DataSource source) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(source.readString((int) source.size()));
        return new Gson().toJsonTree(data).getAsJsonObject();
    }

    @Override
    public boolean canRead(AssetID id) {
        if (id instanceof EmperorAssetId emperorAssetId) {
            return emperorAssetId.inferAssetType() == AssetType.DATA && emperorAssetId.fileName().endsWith(".resource");
        }
        return false;
    }
}
