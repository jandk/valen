package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class DoomGameFactory implements GameFactory<DoomGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("DOOMx64.exe", "DOOMx64vk.exe");
    }

    @Override
    public DoomGame load(Path path) {
        return new DoomGame(path.getParent());
    }
}
