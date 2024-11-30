package be.twofold.valen.ui;

import be.twofold.valen.ui.viewer.*;
import be.twofold.valen.ui.window.*;
import dagger.*;
import jakarta.inject.*;

@Singleton
@Component(modules = {
    ViewModule.class,
    ViewerModule.class,
})
interface PresenterFactory {
    MainPresenter presenter();
}
