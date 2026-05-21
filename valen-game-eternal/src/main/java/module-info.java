module valen.game.eternal {
    requires com.google.gson;
    requires org.slf4j;
    requires valen.core;
    requires valen.game.idtech;

    requires static java.sql; // For import only

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.eternal.EternalGameFactory;
}
