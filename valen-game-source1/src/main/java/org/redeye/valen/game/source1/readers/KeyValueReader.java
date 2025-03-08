package org.redeye.valen.game.source1.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.source1.*;
import org.redeye.valen.game.source1.utils.keyvalues.*;

import java.io.*;
import java.util.*;

public final class KeyValueReader implements AssetReader<VdfValue, SourceAsset> {
    private static final Set<String> SUPPORTED = Set.of("res", "vdf", "vmt");

    @Override
    public boolean canRead(SourceAsset asset) {
        return SUPPORTED.contains(asset.id().extension());
    }

    @Override
    public VdfValue read(DataSource source, SourceAsset asset) throws IOException {
        var string = source.readString(Math.toIntExact(source.size()));
        return new VdfReader(new StringReader(string)).parse();
    }
}
