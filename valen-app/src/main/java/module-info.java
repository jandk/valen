module valen.app {
    requires com.google.gson;
    requires java.desktop;
    requires java.sql;
    requires javafx.graphics;
    requires valen.core;
    requires valen.export.gltf;
    requires valen.lib;
    requires valen.ui;
    requires valen.export;

    uses be.twofold.valen.export.Exporter;

    exports be.twofold.valen;
}
