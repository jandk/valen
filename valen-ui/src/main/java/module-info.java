module valen.ui {
    requires com.google.gson;
    requires dagger;
    requires jakarta.inject;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.jetbrains.annotations;
    requires org.slf4j;
    requires valen.core;
    requires java.desktop;
    requires java.sql;

    exports be.twofold.valen.ui;

    opens be.twofold.valen.ui.common.settings to com.google.gson;
    opens be.twofold.valen.ui.component.settings to javafx.fxml;
    exports be.twofold.valen.ui.common;
}
