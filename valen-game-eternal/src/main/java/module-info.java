module valen.game.eternal {
    requires com.google.gson;
    requires java.sql; // For import only
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.eternal.EternalGameFactory;
}
