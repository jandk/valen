module valen.game.source1 {
    requires java.desktop; // For testing only
    requires java.sql; // For import only
    requires valen.core;
    requires be.twofold.tinybcdec;


    exports org.redeye.valen.game.source1;

    provides be.twofold.valen.core.game.GameFactory
        with org.redeye.valen.game.source1.HalfLife2GameFactory,
            org.redeye.valen.game.source1.PortalGameFactory,
            org.redeye.valen.game.source1.TeamFortres2GameFactory;
}
