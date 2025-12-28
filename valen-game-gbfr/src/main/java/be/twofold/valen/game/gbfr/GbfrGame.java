package be.twofold.valen.game.gbfr;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GbfrGame implements Game {
    private final Path path;

    public GbfrGame(Path path) {
        this.path = Check.nonNull(path, "path");
//        try {
//            loadArchive("data.i");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public List<String> archiveNames() {
        return List.of("data.i");
    }

    @Override
    public Archive<?, ?> loadArchive(String name) throws IOException {
        return new GbfrArchive(path.resolve(name));
    }
}
