import be.twofold.valen.game.eternal.*;

module valen.game.eternal {
    requires com.sun.jna; // For testing only
    requires com.google.gson;
    requires java.desktop; // For testing only
    requires java.sql; // For import only
    requires valen.core;

    exports be.twofold.valen.game.eternal;
    exports be.twofold.valen.game.eternal.resource;

    uses be.twofold.valen.core.game.GameFactory;

    provides be.twofold.valen.core.game.GameFactory
        with EternalGameFactory;
}
