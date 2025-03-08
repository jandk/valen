module valen.game.source {
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory with
        org.redeye.valen.game.source1.HalfLife2GameFactory,
        org.redeye.valen.game.source1.PortalGameFactory,
        org.redeye.valen.game.source1.TeamFortress2GameFactory;
}
