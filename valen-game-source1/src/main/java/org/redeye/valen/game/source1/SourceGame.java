package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.source1.providers.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SourceGame implements Game {
    private final List<Provider> providers = new ArrayList<>();


    public SourceGame(Path path, String mod) throws IOException {
        var mainGamePath = path.resolve(mod);
        var provider = new GameinfoProvider(mainGamePath.resolve("gameinfo.txt"));
        providers.add(provider);
    }

    @Override
    public List<String> archiveNames() {
        return providers.stream().map(Provider::getName).toList();
    }

    @Override
    public Archive loadArchive(String name) throws IOException {
        return providers.stream().filter(provider -> provider.getName().equals(name)).findFirst().orElseThrow();
    }

}
