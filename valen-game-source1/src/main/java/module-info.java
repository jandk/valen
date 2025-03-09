module valen.game.source {
    requires org.slf4j;
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory with
        org.redeye.valen.game.source1.SourceGameFactory;
}
