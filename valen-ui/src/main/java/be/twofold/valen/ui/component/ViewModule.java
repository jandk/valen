package be.twofold.valen.ui.component;

import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.component.main.*;
import be.twofold.valen.ui.component.modelviewer.*;
import be.twofold.valen.ui.component.rawview.*;
import be.twofold.valen.ui.component.settings.*;
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
    abstract ModelView bindModelView(ModelFXView modelView);

    @Binds
    abstract RawView bindRawView(RawFXView rawView);

    @Binds
    abstract SettingsView bindOptionsView(SettingsFXView settingsView);

    @Binds
    abstract TextureView bindTextureView(TextureFXView textureView);

}
