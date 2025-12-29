module valen.game.goldsrc {
    requires org.slf4j;
    requires valen.core;
    requires wtf.reversed.toolbox;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.goldsrc.GoldSrcGameFactory;
}
