package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.doom.resources.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

class DoomArchive implements Archive {
    private final ResourcesIndex index;

    DoomArchive(Path base, String name) throws IOException {
        this.index = ResourcesIndex.read(base.resolve(name + ".index"));
    }

    @Override
    public List<Asset> assets() {
        return index.entries().stream()
            .map(e -> new Asset(new DoomAssetID(e.resourceName()), AssetType.BINARY, e.size(), Map.of()))
            .toList();
    }

    @Override
    public boolean exists(AssetID identifier) {
        return false;
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) {
        return null;
    }
}
