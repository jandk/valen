package be.twofold.valen.game.goldsrc;

import be.twofold.valen.core.game.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GoldSrcGame implements Game {
    private static final Logger log = LoggerFactory.getLogger(GoldSrcGame.class);
    private final Path base;

    public GoldSrcGame(Path base) {
        this.base = base;
    }

    @Override
    public List<String> archiveNames() {
        try (var list = Files.list(base)) {
            return list
                .filter(path -> Files.isRegularFile(path.resolve("liblist.gam")))
                .map(path -> path.getFileName().toString())
                .toList();
        } catch (IOException e) {
            log.warn("Failed to list archives", e);
            return List.of();
        }
    }

    @Override
    public GoldSrcArchive loadArchive(String name) throws IOException {
        return new GoldSrcArchive(base.resolve(name));
    }
}
