package be.twofold.valen.game.fear;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.fear.reader.ltarchive.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class FearArchive implements Archive {
    private final DataSource source;
    private final LTArchive archive;

    public FearArchive(Path path) throws IOException {
        this.source = DataSource.fromPath(path);
        this.archive = LTArchive.read(source);
    }

    @Override
    public List<Asset> assets() {
        for (var entry : archive.directoryEntries()) {

        }
        return List.of();
    }

    @Override
    public boolean exists(AssetID identifier) {
        return false;
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        return null;
    }
}
