module valen.ui {
    requires com.google.gson;
    requires dagger;
    requires jakarta.inject;
    requires javafx.controls;
    requires javafx.graphics;
    requires valen.core;
    requires org.slf4j;

    exports be.twofold.valen.ui;

    opens be.twofold.valen.ui.common.settings to com.google.gson;
    exports be.twofold.valen.ui.common;
}
