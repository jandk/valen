module valen.game.greatcircle {
    requires com.google.gson;
    requires java.sql; // For import only
    requires org.slf4j;
    requires valen.core;
    requires valen.game.idtech;
    requires java.xml.crypto;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.greatcircle.GreatCircleGameFactory;

    opens be.twofold.valen.game.greatcircle;
    opens be.twofold.valen.game.greatcircle.resource;

    opens be.twofold.valen.game.greatcircle.reader.packagemapspec to com.google.gson;

    exports be.twofold.valen.game.greatcircle.reader.image to valen.core;
    exports be.twofold.valen.game.greatcircle.defines to valen.core;
}
