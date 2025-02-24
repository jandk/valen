package be.twofold.valen.game.eternal.reader.file;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;

import java.io.*;
import java.nio.*;

public final class FileReader implements AssetReader<ByteBuffer, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.key().type() == ResourceType.File;
    }

    @Override
    public ByteBuffer read(DataSource source, EternalAsset resource) throws IOException {
        return File.read(source).data();
    }
}
