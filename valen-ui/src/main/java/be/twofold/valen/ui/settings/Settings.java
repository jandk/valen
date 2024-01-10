package be.twofold.valen.ui.settings;

import java.nio.file.*;
import java.util.*;

public final class Settings {
    private Path gameDirectory;

    public Optional<Path> getGameDirectory() {
        return Optional.ofNullable(gameDirectory);
    }

    public void setGameDirectory(Path gameDirectory) {
        this.gameDirectory = gameDirectory;
    }
}
