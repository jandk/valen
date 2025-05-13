module valen.game.darkages {
    requires com.google.gson;
    requires org.slf4j;
    requires valen.core;
    requires valen.game.idtech;

    requires static java.sql; // For import only

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.darkages.DarkAgesGameFactory;

    exports be.twofold.valen.game.darkages.reader.image;
    exports be.twofold.valen.game.darkages.reader.streamdb;
}
