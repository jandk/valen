package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.archives.*;
import org.redeye.valen.game.spacemarines2.fio.*;
import org.redeye.valen.game.spacemarines2.serializers.scene.*;
import org.redeye.valen.game.spacemarines2.types.*;

import java.io.*;
import java.util.*;

public class ClassListReader implements Reader<List<ScnInstanceClassData>> {
    @Override
    public List<ScnInstanceClassData> read(EmperorArchive archive, Asset asset, DataSource source) throws IOException {
        ResourceHeader ignored = ResourceHeader.read(source);

        var serializer = new FioArraySerializer<>(ScnInstanceClassData::new, new ScnInstanceClassDataSerializer());
        var instances = serializer.load(source);
        return instances;
    }

    @Override
    public boolean canRead(AssetID asset) {
        return (asset instanceof EmperorAssetId) && asset.fileName().endsWith(".class_list");
    }
}
