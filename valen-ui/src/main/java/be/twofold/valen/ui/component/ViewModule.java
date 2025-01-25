package be.twofold.valen.ui.component;

import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.component.main.*;
import be.twofold.valen.ui.component.modelviewer.*;
import be.twofold.valen.ui.component.textureviewer.*;
import dagger.Module;
import dagger.*;

@Module
public abstract class ViewModule {

    @Binds
    abstract MainView bindMainView(MainFXView mainView);

    @Binds
    abstract FileListView bindFileListView(FileListFXView fileListView);

    @Binds
    abstract ModelView bindModelView(ModelFXView textureView);

    @Binds
    abstract TextureView bindTextureView(TextureFXView textureView);

}
