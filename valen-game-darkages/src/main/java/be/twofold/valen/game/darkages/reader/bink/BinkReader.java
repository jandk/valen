package be.twofold.valen.game.darkages.reader.bink;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.resources.*;

import java.io.*;
import java.nio.*;

public final class BinkReader implements AssetReader<ByteBuffer, DarkAgesAsset> {
    private final DarkAgesArchive archive;

    public BinkReader(DarkAgesArchive archive) {
        this.archive = Check.notNull(archive);
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.Bink;
    }

    @Override
    public ByteBuffer read(DataSource source, DarkAgesAsset asset) throws IOException {
        long hash = Hash.hash(asset.hash(), 0, 0);
        return archive.readStream(hash, -1);
    }
}
