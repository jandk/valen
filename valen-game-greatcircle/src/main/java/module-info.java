module valen.game.greatcircle {
    requires com.google.gson;
    requires java.sql; // For import only
    requires org.slf4j;
    requires valen.core;

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.greatcircle.GreatCircleGameFactory;

    opens be.twofold.valen.game.greatcircle;
    opens be.twofold.valen.game.greatcircle.resource;

    opens be.twofold.valen.game.greatcircle.reader.packagemapspec to com.google.gson;
}
