package be.twofold.valen.game.eternal.reader.file;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;

public final class FileReader implements AssetReader<File, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.key().type() == ResourceType.File;
    }

    @Override
    public File read(DataSource source, EternalAsset resource) throws IOException {
        return File.read(source);
    }
}
