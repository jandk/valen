package be.twofold.valen.game.eternal.reader.json;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;

public final class JsonReader implements AssetReader<Bytes, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.Json;
    }

    @Override
    public Bytes read(BinaryReader reader, EternalAsset asset) throws IOException {
        int size = Math.toIntExact(reader.readLongBE());
        return reader.readBytesStruct(size);
    }
}
