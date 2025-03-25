package be.twofold.valen.ui;

import be.twofold.valen.ui.common.event.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.main.*;
import dagger.*;
import jakarta.inject.*;

import java.util.*;

@Singleton
@Component(modules = {
    ControllerModule.class,
    SettingsModule.class,
    ViewModule.class,
    ViewerModule.class,
})
public interface MainFactory {

    Map<Class<?>, Provider<Controller>> controllerProviders();

    EventBus eventBus();

    MainPresenter presenter();

    Settings settings();

}
