module valen.ui {
    requires com.google.gson;
    requires java.desktop;
    requires javafx.controls;
    requires valen.core;
    requires valen.lib;

    exports be.twofold.valen.ui;
    exports be.twofold.valen.ui.settings;

    opens be.twofold.valen.ui.settings to com.google.gson;
}
