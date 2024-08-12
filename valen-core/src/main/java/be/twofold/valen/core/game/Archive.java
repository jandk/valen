package be.twofold.valen.core.game;

import java.io.*;
import java.nio.*;
import java.util.*;

public interface Archive<T extends AssetIdentifier> {

    List<Asset<T>> assets();

    boolean exists(T identifier);

    Object loadAsset(T identifier) throws IOException;

    ByteBuffer loadRawAsset(T identifier) throws IOException;

}
