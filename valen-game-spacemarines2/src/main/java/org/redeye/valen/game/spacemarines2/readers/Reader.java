package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.spacemarines2.archives.*;

import java.io.*;

public interface Reader<T> {

    T read(EmperorArchive archive, Asset asset, DataSource source) throws IOException;

    boolean canRead(AssetID asset);
}
