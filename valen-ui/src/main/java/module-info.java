module valen.ui {
    requires com.google.gson;
    requires dagger;
    requires jakarta.inject;
    requires javafx.controls;
    requires javafx.graphics;
    requires valen.core;

    exports be.twofold.valen.ui;
    exports be.twofold.valen.ui.settings;

    opens be.twofold.valen.ui.settings to com.google.gson;

    uses be.twofold.valen.core.game.GameFactory;
}
