package be.twofold.valen.core.game;

import java.io.*;
import java.util.*;

public interface Game extends Closeable {

    List<String> archiveNames();

    AssetLoader open(String name) throws IOException;

    @Override
    default void close() {
        // do nothing
    }
}
