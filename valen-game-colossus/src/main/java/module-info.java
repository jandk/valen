module valen.game.colossus {
    requires valen.core;
    requires com.google.gson;

    exports be.twofold.valen.game.colossus.resource;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.colossus.ColossusGameFactory;
}
