module valen.game.idtech {
    requires com.google.gson;
    requires org.slf4j;
    requires valen.core;

    exports be.twofold.valen.game.idtech.decl.parser;
    exports be.twofold.valen.game.idtech.decl;
    exports be.twofold.valen.game.idtech.defines;
    exports be.twofold.valen.game.idtech.geometry;
    exports be.twofold.valen.game.idtech.material;
    exports be.twofold.valen.game.idtech.renderparm;
}
