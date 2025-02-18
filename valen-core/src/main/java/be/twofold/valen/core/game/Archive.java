package be.twofold.valen.core.game;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public interface Archive {

    Stream<? extends Asset> assets();

    Optional<? extends Asset> getAsset(AssetID identifier);

    <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException;

    default boolean exists(AssetID identifier) {
        return getAsset(identifier).isPresent();
    }

}
