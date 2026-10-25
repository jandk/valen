package be.twofold.valen.core.util.logging;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.logging.*;

public final class FileLogging {
    private static final int BYTES_PER_FILE = 5 * 1024 * 1024;
    private static final int FILES_KEPT = 5;

    private FileLogging() {
    }

    public static Path install() throws IOException {
        var directory = AppDirectories.logs();
        Files.createDirectories(directory);

        var pattern = directory.resolve("valen-%g.log").toString().replace('\\', '/');

        var handler = new FileHandler(pattern, BYTES_PER_FILE, FILES_KEPT, true);
        handler.setFormatter(new PlainFormatter());
        handler.setLevel(Level.ALL);

        Logger.getLogger("").addHandler(handler);
        return directory;
    }
}
