module valen.game.darkages {
    requires com.google.gson;
    requires org.slf4j;
    requires valen.core;
    requires valen.game.idtech;
    requires wtf.reversed.toolbox;

    requires static java.sql;
    requires org.jetbrains.annotations; // For import only

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.darkages.DarkAgesGameFactory;

    exports be.twofold.valen.game.darkages.reader.basemodel;
    exports be.twofold.valen.game.darkages.reader.geometry;
    exports be.twofold.valen.game.darkages.reader.image;
    exports be.twofold.valen.game.darkages.reader.streamdb;
    exports be.twofold.valen.game.darkages.reader;
    exports be.twofold.valen.game.darkages;
}
