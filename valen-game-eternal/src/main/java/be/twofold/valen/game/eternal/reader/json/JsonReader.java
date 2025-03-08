package be.twofold.valen.game.eternal.reader.json;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.nio.*;

public final class JsonReader implements AssetReader<ByteBuffer, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.Json;
    }

    @Override
    public ByteBuffer read(DataSource source, EternalAsset asset) throws IOException {
        int size = Math.toIntExact(source.readLongBE());
        return source.readBuffer(size);
    }
}
