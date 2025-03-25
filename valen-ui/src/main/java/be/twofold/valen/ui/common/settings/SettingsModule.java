package be.twofold.valen.ui.common.settings;

import dagger.Module;
import dagger.*;
import jakarta.inject.*;

@Module
public abstract class SettingsModule {

    @Provides
    @Singleton
    static Settings getSettings() {
        return SettingsManager.get();
    }

}
