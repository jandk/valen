package be.twofold.valen.ui.component;

import be.twofold.valen.ui.component.dataviewer.*;
import be.twofold.valen.ui.component.modelviewer.*;
import be.twofold.valen.ui.component.textureviewer.*;
import be.twofold.valen.ui.component.textviewer.*;
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
