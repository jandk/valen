module valen.ui {
    requires com.google.gson;
    requires java.desktop;
    requires javafx.controls;
    requires javafx.graphics;
    requires jfx.incubator.richtext;
    requires org.slf4j;
    requires valen.core;

    exports be.twofold.valen.ui;

    opens be.twofold.valen.ui.common.settings to com.google.gson;
}
