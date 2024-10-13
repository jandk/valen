package be.twofold.valen.ui.viewer;

import be.twofold.valen.ui.viewer.data.*;
import be.twofold.valen.ui.viewer.model.*;
import be.twofold.valen.ui.viewer.text.*;
import be.twofold.valen.ui.viewer.texture.*;
import dagger.Module;
import dagger.*;
import dagger.multibindings.*;

@Module
public abstract class ViewerModule {

    @Binds
    @IntoSet
    abstract Viewer dataViewer(DataViewer viewer);

    @Binds
    @IntoSet
    abstract Viewer modelViewer(ModelPresenter viewer);

    @Binds
    @IntoSet
    abstract Viewer textureViewer(TexturePresenter viewer);

    @Binds
    @IntoSet
    abstract Viewer textViewer(TextViewer viewer);

}
