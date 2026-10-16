module valen.game.eternal {
    requires com.google.gson;
    requires org.slf4j;
    requires valen.core;
    requires valen.game.idtech;
    requires valen.middleware.wwise;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.eternal.EternalGameFactory;

    opens be.twofold.valen.game.eternal.reader.md6anim to org.junit.platform.commons;

}
