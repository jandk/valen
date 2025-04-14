package be.twofold.valen.ui.component;

import backbonefx.di.*;
import be.twofold.valen.ui.component.settings.*;

public final class ControllerModule {

    @Provides
    public Controller settingsController(SettingsController controller) {
        return controller;
    }

}
