module valen.game.deathloop {
    requires com.sun.jna;
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.deathloop.DeathloopGameFactory;
}
