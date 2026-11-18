package be.twofold.valen.ui.common;

import be.twofold.valen.core.game.*;
import jakarta.inject.*;

/**
 * Shared application state: the loaded game and the open archive.
 * <p>
 * Write from the FX thread, read from anywhere.
 */
@Singleton
public final class Session {
    private final Value<LoadedGame> game = new Value<>();
    private final Value<AssetLoader> loader = new Value<>();

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

    public record LoadedGame(Game game, GameEntry entry) {
    }
}
