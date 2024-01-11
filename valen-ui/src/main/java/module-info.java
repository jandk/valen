module valen.ui {
    requires com.google.gson;
    requires dagger;
    requires java.desktop;
    requires javax.inject;
    requires valen.core;
    requires valen.lib;

    exports be.twofold.valen.ui;
    exports be.twofold.valen.ui.settings;

    opens be.twofold.valen.ui.settings to com.google.gson;
}
