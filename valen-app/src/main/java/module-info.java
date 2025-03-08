module valen.app {
    requires java.desktop;
    requires javafx.graphics;
    requires valen.core;
    requires valen.ui;

    // Exporters
    requires valen.export.dds;
    requires valen.export.gltf;
    requires valen.export.png;

    // Games
    requires valen.game.eternal;

    exports be.twofold.valen;
}
