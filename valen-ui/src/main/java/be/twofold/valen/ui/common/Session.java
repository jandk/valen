package be.twofold.valen.ui.common;

import be.twofold.valen.core.game.*;
import jakarta.inject.*;

import java.util.*;

/**
 * Shared application state: the loaded game and the open archive.
 * <p>
 * Only written by {@code GameService}, on its own thread; read from anywhere.
 */
@Singleton
public final class Session {
    private final Value<LoadedGame> game = new Value<>();
    private final Value<AssetLoader> loader = new Value<>();
    private final Value<List<GameEntry>> installedGames = new Value<>(List.of());

    @Inject
    public Session() {
    }

    public ReadOnlyValue<LoadedGame> gameProperty() {
        return game;
    }

    public LoadedGame getGame() {
        return game.get();
    }

    public void setGame(LoadedGame value) {
        loader.set(null);
        game.set(value);
    }

    public ReadOnlyValue<AssetLoader> loaderProperty() {
        return loader;
    }

    public AssetLoader getLoader() {
        return loader.get();
    }

    public void setLoader(AssetLoader value) {
        loader.set(value);
    }

    public ReadOnlyValue<List<GameEntry>> installedGamesProperty() {
        return installedGames;
    }

    public void setInstalledGames(List<GameEntry> installedGames) {
        this.installedGames.set(List.copyOf(installedGames));
    }

    public record LoadedGame(Game game, GameEntry entry) {
    }
}
