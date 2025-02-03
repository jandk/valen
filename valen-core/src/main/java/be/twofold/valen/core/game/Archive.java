package be.twofold.valen.core.game;

import java.io.*;
import java.util.*;

public interface Archive {

    List<? extends Asset> assets();

    boolean exists(AssetID identifier);

    Asset getAsset(AssetID identifier);

    <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException;

}
