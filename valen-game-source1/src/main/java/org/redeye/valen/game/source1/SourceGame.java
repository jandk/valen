package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record SourceGame(
    Path path,
    List<String> mods
) implements Game {
    public SourceGame {
        Check.notNull(path, "path");
        mods = List.copyOf(mods);
    }

    @Override
    public List<String> archiveNames() {
        return mods;
    }

    @Override
    public Archive loadArchive(String name) throws IOException {
        return new SourceArchive(path.resolve(name));
    }
}
