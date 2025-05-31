package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.archives.*;
import org.redeye.valen.game.spacemarines2.serializers.terrain.*;
import org.redeye.valen.game.spacemarines2.types.*;
import org.redeye.valen.game.spacemarines2.types.terrain.*;

import java.io.*;

public class TerrainReader implements Reader<Terrain> {

    @Override
    public Terrain read(EmperorArchive archive, Asset asset, DataSource source) throws IOException {
        if (!(asset.id() instanceof EmperorAssetId terId)) {
            return null;
        }
        ResourceHeader ignored = ResourceHeader.read(source);
        TerrainSerializer serializer = new TerrainSerializer();
        Terrain terrain = serializer.load(source);
        return terrain;
    }


    @Override
    public boolean canRead(AssetID id) {
        if (id instanceof EmperorAssetId emperorAssetId) {
            return emperorAssetId.fileName().endsWith(".terrain");
        }
        return false;
    }
}
