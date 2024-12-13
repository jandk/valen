module valen.game.goldsrc {
    requires java.sql; // For import only
    requires valen.core;
    requires org.slf4j;
    requires jdk.compiler;

    provides be.twofold.valen.core.game.GameFactory
        with org.redeye.valen.game.halflife.HalfLifeGameFactory;
}
