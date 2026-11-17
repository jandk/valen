package be.twofold.valen.core.game;

import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public interface GameFactory<T extends Game> {

    String displayName();

    int steamAppId();

    Set<String> executableNames();

    T load(Path path) throws IOException;

    default boolean canLoad(Path path) {
        return executableNames().contains(path.getFileName().toString());
    }

    static List<GameEntry> detectInstalled() throws IOException {
        var installed = SteamApps.findAllGames();
        var entries = new ArrayList<GameEntry>();
        for (GameFactory<?> factory : ServiceLoader.load(GameFactory.class)) {
            var appId = factory.steamAppId();
            var dir = installed.get(appId);
            if (dir == null) {
                continue;
            }
            factory.executableNames().stream()
                .map(dir::resolve)
                .filter(Files::isRegularFile)
                .findFirst()
                .ifPresent(exe -> entries.add(new GameEntry(factory.displayName(), exe)));
        }
        return entries;
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
