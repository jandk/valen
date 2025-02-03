package be.twofold.valen.core.game;

import java.io.*;
import java.util.*;

public interface Archive {

    List<? extends Asset> assets();

    Optional<? extends Asset> get(AssetID identifier);

    <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException;

    default boolean exists(AssetID identifier) {
        return get(identifier).isPresent();
    }

}
