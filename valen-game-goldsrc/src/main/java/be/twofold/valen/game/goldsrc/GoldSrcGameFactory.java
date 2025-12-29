package be.twofold.valen.game.goldsrc;

import be.twofold.valen.core.game.*;

import java.nio.file.*;
import java.util.*;

public final class GoldSrcGameFactory implements GameFactory<GoldSrcGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("hl.exe");
    }

    @Override
    public GoldSrcGame load(Path path) {
        return new GoldSrcGame(path.getParent());
    }
}
