package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GustavGameFactory implements GameFactory<GustavGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("bg3.exe");
    }

    @Override
    public GustavGame load(Path path) throws IOException {
        return new GustavGame(path.getParent().getParent());
    }
}
