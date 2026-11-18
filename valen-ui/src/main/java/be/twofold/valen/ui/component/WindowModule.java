package be.twofold.valen.ui.component;

import backbonefx.di.*;
import be.twofold.valen.ui.service.*;
import jakarta.inject.*;
import javafx.stage.*;

public final class WindowModule {
    private final Stage stage;

    public WindowModule(Stage stage) {
        this.stage = stage;
    }

    @Provides
    @Singleton
    public Dialogs dialogs() {
        return new FxDialogs(stage);
    }

    @Provides
    public Stage stage() {
        return stage;
    }

    @Provides
    @Singleton
    public UiThread uiThread() {
        return FX::run;
    }
}
