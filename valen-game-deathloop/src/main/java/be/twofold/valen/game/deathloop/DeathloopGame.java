package be.twofold.valen.game.deathloop;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.game.deathloop.master.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class DeathloopGame implements Game {
    private final Path base;
    private final MasterIndex masterIndex;
    private final Decompressor decompressor;

    public DeathloopGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.masterIndex = MasterIndex.read(base.resolve("master.index"));
        this.decompressor = Decompressor.oodle(path.resolve("oo2core_8_win64.dll"));
    }

    @Override
    public List<String> archiveNames() {
        return List.of("master.index");
    }

    @Override
    public DeathloopArchive loadArchive(String name) throws IOException {
        var indexFile = base.resolve(masterIndex.indexFile());
        var dataFiles = masterIndex.dataFiles().stream()
            .map(base::resolve)
            .toList();
        return new DeathloopArchive(indexFile, dataFiles, decompressor);
    }
}
