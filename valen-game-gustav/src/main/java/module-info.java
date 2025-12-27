module valen.game.gustav {
    requires org.slf4j;
    requires valen.core;
    requires valen.export.dds;
    requires valen.format.granite;
    requires wtf.reversed.toolbox;
    requires org.jetbrains.annotations;

    exports be.twofold.valen.game.gustav.reader.pak to valen.core;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.gustav.GustavGameFactory;
}
