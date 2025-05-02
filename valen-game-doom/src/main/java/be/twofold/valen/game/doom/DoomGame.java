package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DoomGame implements Game {
    private final Path base;

    DoomGame(Path path) {
        this.base = path.resolve("base");
    }

    @Override
    public List<String> archiveNames() {
        return List.of(
            "gameresources",
            "snap_gameresources"
        );
    }

    @Override
    public DoomArchive loadArchive(String name) throws IOException {
        return new DoomArchive(base, name);
    }
}
