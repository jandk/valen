module valen.game.neworder {
    requires com.sun.jna;
    requires valen.core;

    exports be.twofold.valen.game.neworder.index;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.neworder.NewOrderGameFactory;
}
