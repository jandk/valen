package org.redeye.valen.game.halflife2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;

import java.io.*;

public interface Reader<T> {

    T read(Archive archive, Asset asset, DataSource source) throws IOException;

    boolean canRead(Asset asset);
}
