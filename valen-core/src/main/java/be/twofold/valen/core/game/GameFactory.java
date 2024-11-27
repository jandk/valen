package be.twofold.valen.core.game;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public interface GameFactory<T extends Game> {

    Set<String> executableNames();

    T load(Path path) throws IOException;

    default boolean canLoad(Path path) {
        return executableNames().contains(path.getFileName().toString());
    }

    static GameFactory<?> resolve(Path path) {
        return ServiceLoader.load(GameFactory.class).stream()
            .map(ServiceLoader.Provider::get)
            .filter(factory -> factory.canLoad(path))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No GameFactory found for " + path));
    }
}
