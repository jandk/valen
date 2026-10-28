module valen.game.greatcircle {
    requires com.google.gson;
    requires java.xml.crypto;
    requires org.slf4j;
    requires valen.core;
    requires valen.game.idtech;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.greatcircle.GreatCircleGameFactory;

    opens be.twofold.valen.game.greatcircle.reader.packagemapspec to com.google.gson;
}
