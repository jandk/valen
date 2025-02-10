package be.twofold.valen.ui;

import be.twofold.valen.ui.common.event.*;
import be.twofold.valen.ui.common.settings.*;
import be.twofold.valen.ui.component.*;
import be.twofold.valen.ui.component.main.*;
import dagger.*;
import jakarta.inject.*;

@Singleton
@Component(modules = {
    SettingsModule.class,
    ViewModule.class,
    ViewerModule.class,
})
interface MainFactory {

    EventBus eventBus();

    MainPresenter presenter();

    Settings settings();

}
