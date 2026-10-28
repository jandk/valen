module valen.game.darkages {
    requires com.google.gson;
    requires org.slf4j;
    requires valen.core;
    requires valen.game.idtech;
    requires valen.middleware.wwise;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.darkages.DarkAgesGameFactory;
}
