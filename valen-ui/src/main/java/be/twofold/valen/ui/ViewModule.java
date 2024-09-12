package be.twofold.valen.ui;

import be.twofold.valen.ui.viewer.texture.*;
import dagger.Module;
import dagger.*;

@Module
abstract class ViewModule {

    @Binds
    abstract MainView bindMainView(MainViewFx mainView);

    @Binds
    abstract TextureView bindTextureView(TextureViewFx textureView);

}
