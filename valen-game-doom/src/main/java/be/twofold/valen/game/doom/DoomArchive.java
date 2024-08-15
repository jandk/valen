package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.doom.resources.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

class DoomArchive implements Archive {
    private final ResourcesIndex index;

    DoomArchive(Path base, String name) throws IOException {
        try (var source = new ChannelDataSource(Files.newByteChannel(base.resolve(name + ".index")))) {
            this.index = ResourcesIndex.read(source);
        }
    }

    @Override
    public List<Asset> assets() {
        return index.entries().stream()
            .map(e -> new Asset(new DoomAssetID(e.name2()), AssetType.Binary, e.size(), Map.of()))
            .toList();
    }

    @Override
    public boolean exists(AssetID id) {
        return false;
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID id) throws IOException {
        return null;
    }

    @Override
    public Object loadAsset(AssetID id) throws IOException {
        return null;
    }
}
