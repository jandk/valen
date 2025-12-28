package be.twofold.valen.game.gbfr;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GbfrGameFactory implements GameFactory<GbfrGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("granblue_fantasy_relink.exe");
    }

    @Override
    public GbfrGame load(Path path) throws IOException {
        return new GbfrGame(path.getParent());
    }
}
