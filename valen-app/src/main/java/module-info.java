module valen.app {
    requires java.logging;
    requires javafx.graphics;
    requires org.slf4j;
    requires valen.core;
    requires valen.ui;

    // Exporters
    requires valen.export.dds;
    requires valen.export.gltf;
    requires valen.export.png;

    // Games
    requires valen.game.darkages;
    requires valen.game.eternal;
    requires valen.game.greatcircle;
    requires be.twofold.tinybcdec;
    requires valen.game.idtech;
    requires valen.format.png;
    requires com.google.gson;

    exports be.twofold.valen;

    opens be.twofold.valen to com.google.gson;
}
