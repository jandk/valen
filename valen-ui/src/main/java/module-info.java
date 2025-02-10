module valen.ui {
    requires com.google.gson;
    requires dagger;
    requires jakarta.inject;
    requires javafx.controls;
    requires javafx.graphics;
    requires org.jetbrains.annotations;
    requires org.slf4j;
    requires valen.core;

    exports be.twofold.valen.ui;

    opens be.twofold.valen.ui.common.settings to com.google.gson;
    exports be.twofold.valen.ui.common;
}
