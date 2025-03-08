package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.source1.providers.*;
import org.redeye.valen.game.source1.readers.*;
import org.redeye.valen.game.source1.readers.vtf.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class SourceGame implements Game {
    private final List<Provider> providers = new ArrayList<>();
    private final AssetReaders<SourceAsset> readers = new AssetReaders<>(List.of(
        new KeyValueReader(),
        new VmtReader(),
        new VtfReader()
    ));

    public SourceGame(Path path, String mod) throws IOException {
        var resolved = path.resolve(mod).resolve("gameinfo.txt");
        var provider = new GameinfoProvider(resolved);
        providers.add(provider);
    }

    @Override
    public List<String> archiveNames() {
        return providers.stream()
            .map(Provider::getName)
            .toList();
    }

    @Override
    public Archive loadArchive(String name) {
        return providers.stream()
            .filter(provider -> provider.getName().equals(name))
            .findFirst().orElseThrow();
    }
}
