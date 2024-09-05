package org.redeye.valen.game.source1.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.source1.*;
import org.redeye.valen.game.source1.utils.keyvalues.*;

import java.io.*;
import java.util.*;

public class KeyValueReader implements Reader<VdfValue> {

    static Set<String> SUPPORTED_KV_EXT = Set.of("vmt", "vdf", "res");

    @Override
    public VdfValue read(Archive archive, Asset asset, DataSource source) throws IOException {
        byte[] bytes = source.readBytes(Math.toIntExact(source.size()));
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        InputStreamReader streamReader = new InputStreamReader(in);
        VdfReader reader = new VdfReader(streamReader);
        return reader.parse();
    }

    @Override
    public boolean canRead(Asset asset) {
        if (asset.id() instanceof SourceAssetID sid) {
            return SUPPORTED_KV_EXT.contains(sid.extension());
        }
        return false;
    }
}
