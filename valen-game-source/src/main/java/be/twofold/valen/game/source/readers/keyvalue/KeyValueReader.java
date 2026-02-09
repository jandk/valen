package be.twofold.valen.game.source.readers.keyvalue;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.source.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class KeyValueReader implements AssetReader<KeyValue, SourceAsset> {
    private static final Set<String> SUPPORTED = Set.of("res", "vdf", "vmt");

    @Override
    public boolean canRead(SourceAsset asset) {
        return SUPPORTED.contains(asset.id().extension());
    }

    @Override
    public KeyValue read(BinarySource source, SourceAsset asset, LoadingContext context) throws IOException {
        var string = source.readString(Math.toIntExact(source.size()));
        return KeyValue.parse(string);
    }
}
