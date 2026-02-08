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
    public MainView bindMainView(MainViewImpl mainView) {
        return mainView;
    }

    @Provides
    public FileListView bindFileListView(FileListController fileListView) {
        return fileListView;
    }

    @Provides
    public SettingsView bindSettingsView(SettingsController settingsView) {
        return settingsView;
    }

    @Provides
    public ModelView bindModelView(ModelViewImpl modelView) {
        return modelView;
    }

    @Provides
    public RawView bindRawView(RawViewImpl rawView) {
        return rawView;
    }

    @Provides
    public TextureView bindTextureView(TextureViewImpl textureView) {
        return textureView;
    }

}
