package be.twofold.valen.game.dyinglight;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DyingLightGameFactory implements GameFactory<DyingLightGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("DyingLightGame_TheBeast_x64_rwdi.exe");
    }

    @Override
    public DyingLightGame load(Path path) throws IOException {
        for (Path p = path; p != null; p = p.getParent()) {
            Path fs = p.resolve("fs.ini");
            if (Files.exists(fs)) {
                return new DyingLightGame(p);
            }
        }
        throw new IOException("Unable to find the game directory");
    }
}
