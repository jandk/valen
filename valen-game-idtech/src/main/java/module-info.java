module valen.game.idtech {
    requires com.google.gson;
    requires org.slf4j;
    requires valen.core;

    exports be.twofold.valen.game.idtech.game;
    exports be.twofold.valen.game.idtech.geometry;
    exports be.twofold.valen.game.idtech.lexer;
    exports be.twofold.valen.game.idtech.reader.decl.material2;
    exports be.twofold.valen.game.idtech.reader.decl.parser;
    exports be.twofold.valen.game.idtech.reader.decl.renderparm;
    exports be.twofold.valen.game.idtech.reader.decl;
    exports be.twofold.valen.game.idtech.reader.image;
    exports be.twofold.valen.game.idtech.reader.mask;
    exports be.twofold.valen.game.idtech.reader.soundcontainer;
    exports be.twofold.valen.game.idtech.reader.soundmetadata;
}
