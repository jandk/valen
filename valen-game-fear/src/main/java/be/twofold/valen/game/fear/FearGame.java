package be.twofold.valen.game.fear;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class FearGame implements Game {
    private final Path base;

    public FearGame(Path base) {
        this.base = Check.notNull(base);
    }

    @Override
    public List<String> archiveNames() {
        return List.of(
            "FEAR",
            "FEARA",
            "FEARL",
            "FEARE"
        );
    }

    @Override
    public Archive loadArchive(String name) throws IOException {
        return new FearArchive(base.resolve(name + ".Arch00"));
    }
}
