module valen.app {
    requires com.formdev.flatlaf;
    requires com.formdev.flatlaf.extras;
    requires com.google.gson;
    requires com.sun.jna;
    requires java.desktop;
    requires java.sql;
    requires javafx.controls;
    requires valen.core;
    requires valen.export.gltf;
    requires valen.lib;
    requires valen.ui;

    exports be.twofold.valen to javafx.graphics;
}
