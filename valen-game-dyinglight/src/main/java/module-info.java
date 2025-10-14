module valen.game.dyinglight {
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.dyinglight.DyingLightGameFactory;
}