package be.twofold.valen.game.eternal.reader.file;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class FileReader implements AssetReader.Binary<Bytes, EternalAsset> {
    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.of("file");
    }

    @Override
    public Bytes read(BinarySource source, EternalAsset asset, LoadingContext context) throws IOException {
        return File.read(source).data();
    }
}
