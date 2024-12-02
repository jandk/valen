package be.twofold.valen.ui;

import be.twofold.valen.ui.viewer.model.*;
import be.twofold.valen.ui.viewer.texture.*;
import be.twofold.valen.ui.window.*;
import dagger.Module;
import dagger.*;

@Module
abstract class ViewModule {

    @Binds
    abstract MainView bindMainView(MainFXView mainView);

    @Binds
    abstract ModelView bindModelView(ModelFXView textureView);

    @Binds
    abstract TextureView bindTextureView(TextFXView textureView);

}
