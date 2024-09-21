package be.twofold.valen.game.deathloop;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DeathloopGameFactory implements GameFactory<DeathloopGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("Deathloop.exe");
    }

    @Override
    public DeathloopGame load(Path path) throws IOException {
        return new DeathloopGame(path.getParent());
    }
}
