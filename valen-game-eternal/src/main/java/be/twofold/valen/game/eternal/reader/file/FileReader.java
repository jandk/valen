package be.twofold.valen.game.eternal.reader.file;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.reader.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;

public final class FileReader implements ResourceReader<File> {
    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.File;
    }

    @Override
    public File read(DataSource source, Asset<ResourceKey> asset) throws IOException {
        return File.read(source);
    }
}
