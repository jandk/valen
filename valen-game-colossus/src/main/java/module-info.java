module valen.game.colossus {
    requires com.google.gson;
    requires org.slf4j;
    requires valen.core;
    requires wtf.reversed.toolbox;
    requires valen.game.idtech;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.colossus.ColossusGameFactory;
}
