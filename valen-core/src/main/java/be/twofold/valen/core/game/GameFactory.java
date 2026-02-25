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

    static Optional<GameFactory<?>> resolve(Path path) {
        // Do a good old foreach, because streams mess with the generic argument
        for (GameFactory<?> gameFactory : ServiceLoader.load(GameFactory.class)) {
            if (gameFactory.canLoad(path)) {
                return Optional.of(gameFactory);
            }
        }
        return Optional.empty();
    }

}
