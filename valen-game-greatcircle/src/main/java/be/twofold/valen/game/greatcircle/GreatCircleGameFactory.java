package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GreatCircleGameFactory implements GameFactory<GreatCircleGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("TheGreatCircle.exe");
    }

    @Override
    public GreatCircleGame load(Path path) throws IOException {
        return new GreatCircleGame(path.getParent());
    }
}
