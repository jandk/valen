package be.twofold.valen.game.fear;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class FearGameFactory implements GameFactory<FearGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("FEAR.exe");
    }

    @Override
    public FearGame load(Path path) throws IOException {
        return new FearGame(path.getParent());
    }
}
