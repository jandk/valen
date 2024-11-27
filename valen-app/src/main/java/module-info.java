module valen.app {
    requires com.google.gson;
    requires java.desktop;
    requires java.sql;
    requires javafx.graphics;
    requires valen.core;
    requires valen.game.eternal;
    requires valen.ui;

    exports be.twofold.valen;
}
