module valen.ui {
    requires backbonefx;
    requires com.google.gson;
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

    opens be.twofold.valen.ui.common.settings to backbonefx, com.google.gson;
    opens be.twofold.valen.ui.component to backbonefx;
    opens be.twofold.valen.ui.component.filelist to backbonefx;
    opens be.twofold.valen.ui.component.main to backbonefx;
    opens be.twofold.valen.ui.component.modelviewer to backbonefx;
    opens be.twofold.valen.ui.component.preview to backbonefx;
    opens be.twofold.valen.ui.component.progress to backbonefx, javafx.fxml;
    opens be.twofold.valen.ui.component.rawview to backbonefx;
    opens be.twofold.valen.ui.component.settings to backbonefx, javafx.fxml;
    opens be.twofold.valen.ui.component.textureviewer to backbonefx;

    exports be.twofold.valen.ui.common;
}
