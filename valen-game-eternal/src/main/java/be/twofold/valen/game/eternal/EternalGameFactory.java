package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class EternalGameFactory implements GameFactory<EternalGame> {
    @Override
    public String displayName() {
        return "DOOM Eternal";
    }

    @Override
    public int steamAppId() {
        return 782330;
    }

    @Override
    public Set<String> executableNames() {
        return Set.of("DOOMEternalx64vk.exe");
    }

    @Override
    public EternalGame load(Path path) throws IOException {
        return new EternalGame(path.getParent());
    }
}
