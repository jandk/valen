package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.spacemarines2.archives.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SpaceMarines2Game implements Game {
    private final Path base;

    SpaceMarines2Game(Path path) {
        this.base = path;
    }

    @Override
    public List<String> archiveNames() {
        return List.of(
            "client_pc",
            "server_pc"
        );
    }

    @Override
    public Archive loadArchive(String name) throws IOException {
        return new PackArchive(base, name);
    }
}
