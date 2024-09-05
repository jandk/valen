module valen.ui {
    requires be.twofold.tinybcdec;
    requires com.google.gson;
    requires dagger;
    requires jakarta.inject;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.graphics;
    requires valen.core;

    exports be.twofold.valen.ui;
    exports be.twofold.valen.ui.settings;

    opens be.twofold.valen.ui.settings to com.google.gson;
    exports be.twofold.valen.ui.viewers;
    exports be.twofold.valen.ui.viewers.image;
    exports be.twofold.valen.ui.viewers.data;

    uses be.twofold.valen.core.game.GameFactory;
}
