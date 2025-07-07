module valen.app {
    requires java.logging;
    requires javafx.graphics;
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

    exports be.twofold.valen;
}
