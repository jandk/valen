package be.twofold.valen.ui.common.settings;

import backbonefx.di.*;
import jakarta.inject.*;

public final class SettingsModule {

    @Provides
    @Singleton
    public Settings getSettings() {
        return SettingsManager.get();
    }

}
