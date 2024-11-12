package be.twofold.valen.core.game;

import java.io.*;
import java.util.*;

public interface Archive {

    List<Asset> assets();

    boolean exists(AssetID identifier);

    Object loadAsset(AssetID identifier) throws IOException;

    byte[] loadRawAsset(AssetID identifier) throws IOException;

}
