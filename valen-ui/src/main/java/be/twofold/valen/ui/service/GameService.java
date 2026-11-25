package be.twofold.valen.ui.service;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.common.settings.*;
import org.slf4j.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public final class GameService {
    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        var thread = new Thread(runnable, "game-loader");
        thread.setDaemon(true);
        return thread;
    });

    private final Session session;
    private final Settings settings;
    private final Dialogs dialogs;

    public GameService(Session session, Settings settings, Dialogs dialogs) {
        this.session = session;
        this.settings = settings;
        this.dialogs = dialogs;
    }

    public void start() {
        settings.getGameExecutable()
            .filter(Files::isRegularFile)
            .ifPresent(this::load);

        CompletableFuture
            .supplyAsync(() -> {
                try {
                    return GameFactory.detectInstalled();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .exceptionally(e -> {
                log.warn("Could not scan Steam libraries", e);
                return List.of();
            })
            .thenAccept(games -> executor.execute(() -> {
                session.setInstalledGames(games);
                if (session.getGame() == null && games.size() == 1) {
                    load(games.getFirst().executable());
                }
            }));
    }

    public void load(Path executable) {
        executor.execute(() -> {
            if (!Files.isRegularFile(executable)) {
                dialogs.showError("Game not found", executable + " does not exist.");
                return;
            }

            var gameFactory = GameFactory.resolve(executable);
            if (gameFactory.isEmpty()) {
                dialogs.showError("Unsupported game", executable.getFileName() + " is not a game Valen can open.");
                return;
            }

            Game newGame;
            try {
                newGame = gameFactory.get().load(executable);
                settings.setGameExecutable(executable);
            } catch (Exception e) {
                log.error("Could not load game", e);
                dialogs.showException("Could not load game", e);
                return;
            }

            var entry = new GameEntry(gameFactory.get().displayName(), executable);
            var oldGame = session.getGame();
            session.setGame(new Session.LoadedGame(newGame, entry));
            if (oldGame != null) {
                oldGame.game().close();
            }
        });
    }

    public void browse() {
        dialogs.chooseGameExecutable()
            .thenAccept(path -> path.ifPresent(this::load))
            .exceptionally(e -> {
                log.error("Could not choose game", e);
                dialogs.showException("Could not choose game", e);
                return null;
            });
    }

    public void openArchive(String name) {
        if (name == null) {
            return;
        }

        var game = session.getGame();
        executor.execute(() -> {
            if (game == null || game != session.getGame()) {
                return;
            }
            try {
                session.setLoader(game.game().open(name));
            } catch (IOException e) {
                log.error("Could not load archive {}", name, e);
                dialogs.showException("Could not load archive " + name, e);
            }
        });
    }
}
