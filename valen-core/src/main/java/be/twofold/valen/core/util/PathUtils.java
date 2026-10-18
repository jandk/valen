package be.twofold.valen.core.util;

import java.nio.file.*;

public final class PathUtils {
    private PathUtils() {
    }

    public static Path replaceExtension(Path path, String extension) {
        return path.resolveSibling(Filenames.replaceExtension(path.getFileName().toString(), extension));
    }
}
