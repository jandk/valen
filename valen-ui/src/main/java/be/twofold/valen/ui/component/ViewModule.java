package be.twofold.valen.ui.component;

import backbonefx.di.*;
import be.twofold.valen.ui.component.filelist.*;
import be.twofold.valen.ui.component.main.*;
import be.twofold.valen.ui.component.metaview.*;
import be.twofold.valen.ui.component.modelviewer.*;
import be.twofold.valen.ui.component.progress.*;
import be.twofold.valen.ui.component.rawview.*;
import be.twofold.valen.ui.component.settings.*;
import be.twofold.valen.ui.component.textureviewer.*;

public final class ViewModule {

    @Provides
    public FileListView bindFileListView(FileListViewImpl fileListView) {
        return fileListView;
    }

    @Provides
    public MainView bindMainView(MainViewImpl mainView) {
        return mainView;
    }

    @Provides
    public MetaView bindMetaView(MetaViewImpl dataView) {
        return dataView;
    }

    @Provides
    public ModelView bindModelView(ModelViewImpl modelView) {
        return modelView;
    }

    @Provides
    public ProgressView bindProgressView(ProgressViewImpl progressView) {
        return progressView;
    }

    @Provides
    public RawView bindRawView(RawViewImpl rawView) {
        return rawView;
    }

    @Provides
    public SettingsView bindSettingsView(SettingsViewImpl settingsView) {
        return settingsView;
    }

    @Provides
    public TextureView bindTextureView(TextureViewImpl textureView) {
        return textureView;
    }

}
