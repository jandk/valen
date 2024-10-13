module valen.game.eternal {
    requires com.google.gson;
    requires java.desktop; // For testing only
    requires java.sql; // For import only
    requires valen.core;

    exports be.twofold.valen.game.eternal;

    uses be.twofold.valen.core.game.GameFactory;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.eternal.EternalGameFactory;
}
