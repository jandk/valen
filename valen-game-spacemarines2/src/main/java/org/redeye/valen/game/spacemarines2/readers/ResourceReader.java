package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.*;
import org.yaml.snakeyaml.*;

import java.io.*;
import java.util.*;

public class ResourceReader implements Reader<Map<String, ?>> {
    @Override
    public Map<String, ?> read(Archive archive, Asset asset, DataSource source) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(source.readString((int) source.size()));
        return data;
    }

    @Override
    public boolean canRead(AssetID id) {
        if (id instanceof EmperorAssetId emperorAssetId) {
            return emperorAssetId.inferAssetType() == AssetType.Data && emperorAssetId.fileName().endsWith(".resource");
        }
        return false;
    }
}
