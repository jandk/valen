module valen.app {
    requires com.formdev.flatlaf;
    requires com.formdev.flatlaf.extras;
    requires com.google.gson;
    requires java.desktop;
    requires java.sql;
    requires valen.core;
    requires valen.export.gltf;
    requires valen.lib;
    requires valen.ui;

    exports be.twofold.valen to javafx.graphics;
}
