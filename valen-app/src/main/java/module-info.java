module valen.app {
    requires com.google.gson;
    requires java.desktop;
    requires java.sql;
    requires javafx.graphics;
    requires valen.core;
    requires valen.export.gltf;
    requires valen.lib;
    requires valen.ui;

    exports be.twofold.valen;
}
