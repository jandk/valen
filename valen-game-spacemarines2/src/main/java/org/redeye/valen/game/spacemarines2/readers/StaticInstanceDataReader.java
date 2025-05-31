package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.archives.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.io.*;

public class StaticInstanceDataReader implements Reader<StaticInstanceData> {
    @Override
    public StaticInstanceData read(EmperorArchive archive, Asset asset, DataSource source) throws IOException {
        var serializer = new StaticInstanceDataSerializer();
        var instances = serializer.load(source);
        return instances;
    }

    @Override
    public boolean canRead(AssetID asset) {
        return (asset instanceof EmperorAssetId) && asset.fileName().endsWith(".lwi_inst");
    }
}
