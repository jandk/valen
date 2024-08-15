package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ColossusGameFactory implements GameFactory<ColossusGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("NewColossus_x64vk.exe");
    }

    @Override
    public ColossusGame load(Path path) throws IOException {
        return new ColossusGame(path.getParent());
    }
}
