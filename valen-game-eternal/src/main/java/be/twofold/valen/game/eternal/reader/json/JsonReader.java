package be.twofold.valen.game.eternal.reader.json;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;

public final class JsonReader implements AssetReader<Bytes, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.Json;
    }

    @Override
    public Bytes read(BinarySource source, EternalAsset asset) throws IOException {
        int size = source.order(ByteOrder.BIG_ENDIAN).readLongAsInt();
        return source.readBytes(size);
    }
}
