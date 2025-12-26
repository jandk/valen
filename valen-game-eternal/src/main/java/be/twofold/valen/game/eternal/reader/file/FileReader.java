package be.twofold.valen.game.eternal.reader.file;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class FileReader implements AssetReader<Bytes, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.File;
    }

    @Override
    public Bytes read(BinarySource source, EternalAsset resource) throws IOException {
        return File.read(source).data();
    }
}
