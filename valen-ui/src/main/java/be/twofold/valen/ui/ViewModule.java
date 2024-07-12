package be.twofold.valen.ui;

import dagger.Module;
import dagger.*;

@Module
abstract class ViewModule {

    @Binds
    abstract MainView bindMainView(MainViewFx mainView);

}
