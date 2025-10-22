module valen.game.dyinglight {
    requires valen.core;
    requires org.jetbrains.annotations;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.dyinglight.DyingLightGameFactory;
}