module valen.game.deathloop {
    requires com.sun.jna;
    requires valen.core;

    exports be.twofold.valen.game.deathloop.index;
    exports be.twofold.valen.game.deathloop.image;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.deathloop.DeathloopGameFactory;
}
