module valen.game.source {
    requires org.slf4j;
    requires valen.core;
    requires wtf.reversed.toolbox;
    requires org.jetbrains.annotations;

    provides be.twofold.valen.core.game.GameFactory with
        be.twofold.valen.game.source.SourceGameFactory;
}
