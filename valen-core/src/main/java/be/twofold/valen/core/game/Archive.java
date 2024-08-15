package be.twofold.valen.core.game;

import java.io.*;
import java.nio.*;
import java.util.*;

public interface Archive {

    List<Asset> assets();

    boolean exists(AssetID id);

    Object loadAsset(AssetID id) throws IOException;

    ByteBuffer loadRawAsset(AssetID id) throws IOException;

}
