module valen.game.gbfr {
    requires org.slf4j;
    requires valen.core;
    requires valen.format.granite;
    requires wtf.reversed.toolbox;
    requires org.jetbrains.annotations;
    requires flatbuffers.java;
    requires java.sql;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.gbfr.GbfrGameFactory;
}
