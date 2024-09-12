package be.twofold.valen.ui;

import be.twofold.valen.ui.window.*;
import dagger.*;

@Component(modules = ViewModule.class)
public interface PresenterFactory {
    MainPresenter presenter();
}
