package be.twofold.valen.ui.component;

import backbonefx.di.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.component.main.*;
import be.twofold.valen.ui.component.modelviewer.*;
import be.twofold.valen.ui.component.rawview.*;
import be.twofold.valen.ui.component.settings.*;
import be.twofold.valen.ui.component.textureviewer.*;

public final class ViewModule {

    @Provides
    public MainView bindMainView(MainFXView mainView) {
        return mainView;
    }

    @Provides
    public FileListView bindFileListView(FileListFXView fileListView) {
        return fileListView;
    }

    @Provides
    public ModelView bindModelView(ModelFXView modelView) {
        return modelView;
    }

    @Provides
    public RawView bindRawView(RawFXView rawView) {
        return rawView;
    }

    @Provides
    public SettingsView bindOptionsView(SettingsFXView settingsView) {
        return settingsView;
    }

    @Provides
    public TextureView bindTextureView(TextureFXView textureView) {
        return textureView;
    }

}
