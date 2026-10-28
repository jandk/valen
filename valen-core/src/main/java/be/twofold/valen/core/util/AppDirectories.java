package be.twofold.valen.core.util;

import java.nio.file.*;

/**
 * The per-user locations, outside the export directory the user picks.
 */
public final class AppDirectories {
    private AppDirectories() {
    }

    public static Path data() {
        var userHome = System.getProperty("user.home");

        return switch (Platform.OS.current()) {
            case LINUX -> Path.of(userHome, ".config", "valen");
            case WINDOWS -> Path.of(localAppData(userHome), "Valen");
            case MAC -> Path.of(userHome, "Library", "Application Support", "Valen");
        };
    }

    public static Path logs() {
        return data().resolve("logs");
    }

    private static String localAppData(String userHome) {
        var localAppData = System.getenv("LOCALAPPDATA");
        return localAppData != null
            ? localAppData
            : Path.of(userHome, "AppData", "Local").toString();
    }
}
