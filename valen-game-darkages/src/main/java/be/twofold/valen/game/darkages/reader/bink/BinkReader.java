package be.twofold.valen.game.darkages.reader.bink;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public final class BinkReader implements AssetReader<Bytes, DarkAgesAsset> {
    private final BinaryStore<Long> streams;

    public BinkReader(BinaryStore<Long> streams) {
        this.streams = Check.nonNull(streams, "streams");
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.Bink;
    }

    @Override
    public Bytes read(BinarySource source, DarkAgesAsset asset) throws IOException {
        long hash = Hash.hash(asset.hash(), 0, 0);
        return streams.read(hash, -1);
    }
}
