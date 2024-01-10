package be.twofold.valen.ui;

import dagger.*;

@Component(modules = ViewModule.class)
public interface PresenterFactory {
    MainPresenter presenter();
}
