package be.twofold.valen.ui.component;

import be.twofold.valen.ui.component.settings.*;
import dagger.Module;
import dagger.*;
import dagger.multibindings.*;

@Module
public interface ControllerModule {

    @Binds
    @IntoMap
    @ClassKey(SettingsController.class)
    Controller settingsController(SettingsController controller);

}
