module valen.game.greatcircle {
    requires com.google.gson;
    requires java.xml.crypto;
    requires org.slf4j;
    requires valen.core;
    requires valen.game.idtech;
    requires wtf.reversed.toolbox;

    requires static java.sql;
    requires jdk.management.jfr; // For import only

    provides be.twofold.valen.core.game.GameFactory
        with be.twofold.valen.game.greatcircle.GreatCircleGameFactory;

    opens be.twofold.valen.game.greatcircle;
    opens be.twofold.valen.game.greatcircle.resource;

    opens be.twofold.valen.game.greatcircle.reader.packagemapspec to com.google.gson;
}
