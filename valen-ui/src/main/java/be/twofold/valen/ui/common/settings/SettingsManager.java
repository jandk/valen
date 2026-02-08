package be.twofold.valen.ui.common.settings;

import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.settings.gson.*;
import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class SettingsManager {
    private static final Gson GSON = new GsonBuilder()
        .registerTypeHierarchyAdapter(Path.class, new PathTypeAdapter().nullSafe())
        .setPrettyPrinting()
        .create();

    private static final Settings SETTINGS;

    static {
        SETTINGS = load().orElseGet(Settings::new);
        Runtime.getRuntime().addShutdownHook(new Thread(SettingsManager::save));
    }

    public static Settings get() {
        return SETTINGS;
    }

    private static Optional<Settings> load() {
        Path path = determinePath();
        if (!Files.exists(path)) {
            return Optional.empty();
        }

        try {
            return Optional.of(GSON.fromJson(Files.readString(path), Settings.class));
        } catch (IOException | JsonParseException e) {
            return Optional.empty();
        }
    }

    private static void save() {
        try {
            Files.createDirectories(determinePath().getParent());
            Files.writeString(determinePath(), GSON.toJson(SETTINGS));
        } catch (IOException e) {
            // TODO: Better exception handling
            throw new UncheckedIOException(e);
        }
    }

    private static Path determinePath() {
        String userHome = System.getProperty("user.home");

        return switch (OperatingSystem.current()) {
            case Linux -> Path.of(userHome, ".config", "valen", "settings.json");
            case Windows -> Path.of(System.getenv("LOCALAPPDATA"), "Valen", "settings.json");
            case Mac -> Path.of(userHome, "Library", "Application Support", "Valen", "settings.json");
        };
    }
}
