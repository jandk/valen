module valen.app {

    requires java.sql;

    requires com.google.gson;
    requires com.sun.jna;
    requires javafx.controls;

    requires valen.ui;

    exports be.twofold.valen to javafx.graphics;

}
