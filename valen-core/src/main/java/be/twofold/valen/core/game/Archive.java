package be.twofold.valen.core.game;

import java.io.*;
import java.nio.*;

public interface Archive<K extends AssetID, V extends Asset> extends Container<K, V> {

    @Override
    default ByteBuffer read(K key, Integer size) throws IOException {
        return loadAsset(key, ByteBuffer.class);
    }

    <T> T loadAsset(K identifier, Class<T> clazz) throws IOException;

}
