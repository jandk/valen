package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GustavGame implements Game {
    private static final Logger log = LoggerFactory.getLogger(GustavGame.class);

    private final Path dataPath;

    public GustavGame(Path path) {
        this.dataPath = path.resolve("Data");
    }

    @Override
    public List<String> archiveNames() {
        try (var stream = Files.find(dataPath, 2, (p, _) -> matchPath(p))) {
            return stream
                .map(p -> Filenames.removeExtension(dataPath.relativize(p).toString()))
                .toList();
        } catch (IOException e) {
            log.error("Failed to get archive names", e);
            return List.of();
        }
    }

    @Override
    public GustavArchive loadArchive(String name) throws IOException {
        return new GustavArchive(dataPath.resolve(name + ".pak"));
    }

    private boolean matchPath(Path path) {
        return Files.isRegularFile(path)
            && path.getFileName().toString().endsWith(".pak")
            && !path.getFileName().toString().matches("\\w+_\\d+\\.pak");
    }
}
