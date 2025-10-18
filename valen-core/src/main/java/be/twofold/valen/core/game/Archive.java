package be.twofold.valen.core.game;

import be.twofold.valen.core.util.collect.*;

import java.io.*;

public interface Archive<K extends AssetID, V extends Asset> extends Container<K, V> {

    @Override
    default Bytes read(K key, Integer size) throws IOException {
        return loadAsset(key, Bytes.class);
    }

    <T> T loadAsset(K identifier, Class<T> clazz) throws IOException;

}
