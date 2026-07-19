module valen.game.qc {
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.qc.QcGameFactory;
}
