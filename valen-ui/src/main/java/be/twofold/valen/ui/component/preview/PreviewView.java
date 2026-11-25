package be.twofold.valen.ui.component.preview;

import be.twofold.valen.ui.common.*;

public interface PreviewView extends View<PreviewView.Listener> {

    void setMetaView(View<?> view);

    void setRawView(View<?> view);

    void show(View<?> renderer, boolean hasMetadata, boolean hasRaw);

    void setLoading(boolean loading);

    interface Listener extends View.Listener {
    }
}
