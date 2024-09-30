package be.twofold.valen.ui;

import be.twofold.valen.ui.viewer.*;
import be.twofold.valen.ui.window.*;
import dagger.*;

@Component(modules = {
    ViewModule.class,
    ViewerModule.class,
})
public interface PresenterFactory {
    MainPresenter presenter();
}
