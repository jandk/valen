package be.twofold.valen.core.game;

import be.twofold.valen.core.io.*;

import java.io.*;

public interface AssetReader<R, A extends Asset> {

    boolean canRead(A asset);

    R read(DataSource source, A asset) throws IOException;

}
