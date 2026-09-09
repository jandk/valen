package be.twofold.valen.ui.component.audioviewer;

import be.twofold.valen.core.game.*;
import be.twofold.valen.ui.common.*;
import be.twofold.valen.ui.component.*;

public final class AudioPresenter extends AbstractPresenter<AudioView> implements AudioView.Listener, Viewer {
    AudioPresenter(AudioView view) {
        super(view);
        view.setListener(this);
    }

    @Override
    public String getName() {
        return "Audio";
    }

    @Override
    public boolean canPreview(AssetType type) {
        return type == AssetType.AUDIO;
    }

    @Override
    public Object decode(Object data) {
        return null;
    }

    @Override
    public void display(Object payload) {
    }
}
