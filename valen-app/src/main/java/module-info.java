module valen.app {
    requires com.google.gson;
    requires java.desktop;
    requires java.sql;
    requires javafx.graphics;
    requires valen.core;
    requires valen.export.gltf;
    requires valen.export;
    requires valen.game.eternal;
    requires valen.ui;

    uses be.twofold.valen.export.Exporter;
    uses be.twofold.valen.core.game.GameFactory;

    exports be.twofold.valen;
}
