package org.redeye.valen.game.halflife;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.halflife.providers.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class HalfLifeMod implements Game {
    private final Path base;
    private final Map<String, Provider> providers;

    public HalfLifeMod(Path base) {
        this.base = base;
        this.providers = new HashMap<>();
        GameProvider provider = new GameProvider(base);
        this.providers.put("Game", provider);
        for (WadProvider wad : provider.getWads()) {
            this.providers.put(wad.getName(), wad);
        }

    }

    @Override
    public List<String> archiveNames() {
        return providers.keySet().stream().toList();
    }

    @Override
    public Archive loadArchive(String name) throws IOException {
        return providers.get(name);
    }
}
