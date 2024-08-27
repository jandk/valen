module valen.game.halflife2 {
    requires java.desktop; // For testing only
    requires java.sql; // For import only
    requires valen.core;
    requires be.twofold.tinybcdec;


    exports org.redeye.valen.game.halflife2;

    provides be.twofold.valen.core.game.GameFactory
        with org.redeye.valen.game.halflife2.HalfLife2GameFactory;
}
