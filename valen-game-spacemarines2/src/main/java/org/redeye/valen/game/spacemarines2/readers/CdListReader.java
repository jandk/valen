package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.archives.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.io.*;
import java.util.*;

public class CdListReader implements Reader<List<SceneInstanceCreateData>> {
    @Override
    public List<SceneInstanceCreateData> read(EmperorArchive archive, Asset asset, DataSource source) throws IOException {
        ResourceHeader ignored = ResourceHeader.read(source);

        var serializer = new FioArraySerializer<>(SceneInstanceCreateData::new, new InstanceCreateDataSerializer());
        var instances = serializer.load(source);
        return instances;
    }

    @Override
    public boolean canRead(AssetID asset) {
        return (asset instanceof EmperorAssetId) && asset.fileName().endsWith(".cd_list");
    }
}
