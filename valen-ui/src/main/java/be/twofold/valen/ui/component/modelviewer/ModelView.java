package be.twofold.valen.ui.component.modelviewer;

import be.twofold.valen.ui.common.*;

public interface ModelView extends View<View.Listener> {
    void setMeshes(ModelPayload payload);
}
