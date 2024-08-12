module valen.lib {
    requires com.google.gson;
    requires dagger;
    requires java.desktop; // For testing only
    requires java.sql; // For import only
    requires valen.core;

    exports be.twofold.valen.game;
    exports be.twofold.valen.manager;
    exports be.twofold.valen.reader.packagemapspec;
    exports be.twofold.valen.reader.resource;
    exports be.twofold.valen.reader.streamdb;
    exports be.twofold.valen.reader;
    exports be.twofold.valen.resource;

    uses be.twofold.valen.core.game.GameFactory;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.EternalGameFactory;
}
