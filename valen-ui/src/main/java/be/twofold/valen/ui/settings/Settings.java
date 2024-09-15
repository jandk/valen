package be.twofold.valen.ui.settings;

import java.nio.file.*;
import java.util.*;

public final class Settings {
    private Path gameExecutable;

    public Optional<Path> getGameExecutable() {
        return Optional.ofNullable(gameExecutable);
    }

    public void setGameExecutable(Path gameExecutable) {
        this.gameExecutable = gameExecutable;
    }
}
