package be.twofold.valen.ui.component;

import be.twofold.valen.ui.component.modelviewer.*;
import be.twofold.valen.ui.component.rawview.*;
import be.twofold.valen.ui.component.textureviewer.*;
import dagger.*;
import dagger.Module;
import dagger.multibindings.*;

@Module
public abstract class ViewerModule {

//    @Binds
//    @IntoSet
//    abstract Viewer dataViewer(DataViewer viewer);

    @Binds
    @IntoSet
    abstract Viewer modelViewer(ModelPresenter viewer);

    @Binds
    @IntoSet
    abstract Viewer rawViewer(RawPresenter viewer);

    @Binds
    @IntoSet
    abstract Viewer textureViewer(TexturePresenter viewer);

}
