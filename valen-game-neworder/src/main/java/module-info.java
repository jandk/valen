module valen.game.neworder {
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.neworder.NewOrderGameFactory;
}
