module valen.game.deathloop {
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.deathloop.DeathloopGameFactory;
}
